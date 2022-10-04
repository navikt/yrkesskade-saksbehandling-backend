package no.nav.yrkesskade.saksbehandling.service

import com.expediagroup.graphql.generated.enums.BrukerIdType
import com.expediagroup.graphql.generated.journalpost.DokumentInfo
import com.expediagroup.graphql.generated.journalpost.Journalpost
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.yrkesskade.saksbehandling.client.BrevutsendingClient
import no.nav.yrkesskade.saksbehandling.client.bigquery.BigQueryClient
import no.nav.yrkesskade.saksbehandling.client.bigquery.schema.BehandlingPayload
import no.nav.yrkesskade.saksbehandling.client.bigquery.schema.behandling_v1
import no.nav.yrkesskade.saksbehandling.graphql.client.saf.ISafClient
import no.nav.yrkesskade.saksbehandling.model.*
import no.nav.yrkesskade.saksbehandling.util.getLogger
import no.nav.yrkesskade.saksbehandling.util.getSecureLogger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Component
class Dokumentmottak(
    private val behandlingService: BehandlingService,
    @Qualifier("safClient") private val safClient: ISafClient,
    private val bigQueryClient: BigQueryClient,
    private val pdlService: PdlService
) {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()

        /**
         * Utleder dokumentkategori basert på første dokumentinformasjon sin brevkode.
         */
        fun utledDokumentkategori(dokumentInfos: List<DokumentInfo?>?): String {
            val dokumentkategorier: List<String?> =
                dokumentInfos?.mapNotNull { dokumentInfo -> utledDokumentkategori(dokumentInfo?.brevkode) } ?: emptyList()
            return dokumentkategorier.firstOrNull() ?: "ingenBrevkode"
        }

        /**
         * Koder fra https://kodeverk-web.dev.intern.nav.no/kodeverksoversikt/kodeverk/BrevkodeMottak
         */
        private fun utledDokumentkategori(brevkode: String?): String? =
            when (brevkode) {
                "NAV 13-00.08" -> "tannlegeerklaering"
                else -> null // blir ikke mappet
            }
    }

    @Transactional
    fun mottaDokument(dokumentTilSaksbehandlingHendelse: DokumentTilSaksbehandlingHendelse) {
        val dokumentTilSaksbehandling = dokumentTilSaksbehandlingHendelse.dokumentTilSaksbehandling
        val journalpost = hentJournalpostFraSaf(dokumentTilSaksbehandling.journalpostId)
        val foedselsnummer = hentFoedselsnummerFraJournalpost(journalpost)

        val behandling = BehandlingEntity(
            behandlingId = 0,
            tema = journalpost.tema!!.name,
            brukerId = foedselsnummer,
            brukerIdType = BrukerIdType.FNR,
            behandlendeEnhet = dokumentTilSaksbehandling.enhet,
            behandlingstype = Behandlingstype.JOURNALFOERING,
            status = Behandlingsstatus.IKKE_PAABEGYNT,
            behandlingsfrist = Instant.now().plus(30, ChronoUnit.DAYS),
            journalpostId = journalpost.journalpostId,
            dokumentkategori = utledDokumentkategori(journalpost.dokumenter),
            systemreferanse = UUID.randomUUID().toString(),
            framdriftsstatus = Framdriftsstatus.IKKE_PAABEGYNT,
            opprettetTidspunkt = Instant.now(),
            opprettetAv = "yrkesskade-saksbehandling-backend",
            behandlingResultater = emptyList(),
            sak = null,
        )
        val lagretBehandling = behandlingService.lagreBehandling(behandling)
        foerMetrikkIBigQuery(lagretBehandling)
        behandlingService.lagreBehandling(behandling)
    }

    private fun hentFoedselsnummerFraJournalpost(journalpost: Journalpost): String {
        if (journalpost.bruker!!.type == BrukerIdType.FNR) {
            return journalpost.bruker.id!!
        }
        return pdlService.hentFoedselsnummer(journalpost.bruker.id!!)
    }

    /**
     * Avgjør om en journalpost er gyldig (inneholder data som vi kan jobbe med)
     * Kriterier:
     * 1. Det må foreligge dokumenter på journalposten
     * 2. BrukerId må være fødselsnummer/D-nummer, eller aktørId (kan ikke være orgnr)
     *
     * @param journalpost journalposten som skal vurderes
     */
    private fun validerJournalpost(journalpost: Journalpost) {
        logger.info("Validerer journalpost fra SAF med journalpostId ${journalpost.journalpostId}")

        if (journalpost.dokumenter.isNullOrEmpty()) {
            throw RuntimeException("Journalposten mangler dokumenter.")
        }

        if (journalpost.bruker?.id.isNullOrEmpty()) {
            throw RuntimeException("Journalposten mangler brukerinfo.")
        }

        val gyldigeBrukerIdTyper = listOf(BrukerIdType.FNR, BrukerIdType.AKTOERID)
        if (!gyldigeBrukerIdTyper.contains(journalpost.bruker?.type)) {
            throw RuntimeException("BrukerIdType må være en av: $gyldigeBrukerIdTyper, men er: ${journalpost.bruker?.type}")
        }
    }

    private fun hentJournalpostFraSaf(journalpostId: String): Journalpost {
        val journalpost = safClient.hentOppdatertJournalpost(journalpostId)?.journalpost
        if (journalpost == null) {
            logger.error("Fant ikke journalpost i SAF for journalpostId $journalpostId")
            throw RuntimeException("Journalpost med journalpostId $journalpostId finnes ikke i SAF")
        }

        validerJournalpost(journalpost)
        return journalpost
    }

    /**
     * Legger til en rad i BigQuery-metrikkene om en behandling er opprettet/endret.
     *
     * @param behandling behandlingen som ble opprettet
     */
    private fun foerMetrikkIBigQuery(behandling: BehandlingEntity) {
        val payload = BehandlingPayload(
            behandlingId = behandling.behandlingId.toString(),
            journalpostId = behandling.journalpostId,
            utgaaendeJournalpostId = behandling.utgaaendeJournalpostId,
            dokumentkategori = behandling.dokumentkategori,
            behandlingstype = behandling.behandlingstype.kode,
            behandlingsstatus = behandling.status.kode,
            enhetsnr = behandling.behandlendeEnhet ?: "9999",
            overfoertLegacy = false,
            opprettet = behandling.opprettetTidspunkt,
            endret = behandling.endretTidspunkt
        )
        val jsonNode = jacksonObjectMapper().registerModule(JavaTimeModule()).valueToTree<JsonNode>(payload)
        bigQueryClient.insert(
            behandling_v1,
            behandling_v1.transform(jsonNode)
        )
    }

}
