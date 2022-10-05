package no.nav.yrkesskade.saksbehandling.service

import DetaljertBehandling
import com.expediagroup.graphql.generated.enums.BrukerIdType
import com.expediagroup.graphql.generated.journalpost.Bruker
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import hentBrevkode
import hentHovedDokumentTittel
import no.nav.yrkesskade.saksbehandling.client.bigquery.BigQueryClient
import no.nav.yrkesskade.saksbehandling.client.bigquery.schema.BehandlingPayload
import no.nav.yrkesskade.saksbehandling.client.bigquery.schema.behandling_v1
import no.nav.yrkesskade.saksbehandling.client.dokarkiv.FerdigstillJournalpostRequest
import no.nav.yrkesskade.saksbehandling.client.dokarkiv.IDokarkivClient
import no.nav.yrkesskade.saksbehandling.client.oppgave.*
import no.nav.yrkesskade.saksbehandling.graphql.client.saf.ISafClient
import no.nav.yrkesskade.saksbehandling.graphql.common.model.BehandlingsPage
import no.nav.yrkesskade.saksbehandling.graphql.common.model.Behandlingsfilter
import no.nav.yrkesskade.saksbehandling.graphql.common.model.FerdigstillBehandling
import no.nav.yrkesskade.saksbehandling.model.*
import no.nav.yrkesskade.saksbehandling.model.dto.BehandlingDto
import no.nav.yrkesskade.saksbehandling.model.dto.FerdigstiltBehandlingDto
import no.nav.yrkesskade.saksbehandling.repository.BehandlingRepository
import no.nav.yrkesskade.saksbehandling.security.AutentisertBruker
import no.nav.yrkesskade.saksbehandling.util.FristFerdigstillelseTimeManager
import no.nav.yrkesskade.saksbehandling.util.getLogger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.ZoneOffset
import java.util.*

@Service
class BehandlingService(
    private val autentisertBruker: AutentisertBruker,
    private val behandlingRepository: BehandlingRepository,
    private val behandlingsoverfoeringLogService: BehandlingsoverfoeringLogService,
    @Qualifier("dokarkivClient") private val dokarkivClient: IDokarkivClient,
    private val oppgaveClient: OppgaveClient,
    private val pdlService: PdlService,
    @Qualifier("safClient") private val safClient: ISafClient,
    private val bigQueryClient: BigQueryClient,
    @Value("\${application.pretty.name}") private val applicationShortName: String,
    @Value("\${spring.application.name}") private val applicationName: String
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @Transactional
    fun lagreBehandling(behandlingEntity: BehandlingEntity): BehandlingEntity {
        return behandlingRepository.save(behandlingEntity).also {
            logger.info("Lagret behandling (${behandlingEntity.behandlingstype.name}) med journalpostId ${behandlingEntity.journalpostId} og behandlingId ${behandlingEntity.behandlingId}")
            foerMetrikkIBigQuery(it, false)
        }
    }

    fun hentDetaljertBehandling(behandlingId: Long): DetaljertBehandling {
        val behandling = hentBehandling(behandlingId)

        val journalpostResult = safClient.hentOppdatertJournalpost(behandling.journalpostId)
        val dokumenter = if (journalpostResult?.journalpost?.dokumenter != null) {
            val journalpost = journalpostResult.journalpost
            journalpost.dokumenter!!.map {
                val dokument = it!!
                val journalstatus = if (journalpost.journalstatus != null) journalpost.journalstatus.name  else "Status ikke satt"
                val journalposttype = if (journalpost.journalposttype != null) journalpost.journalposttype.name else "Type ikke satt"

                DokumentInfo(dokumentinfoId = dokument.dokumentInfoId, tittel = dokument.tittel.orEmpty(), opprettetTidspunkt = journalpost.datoOpprettet.toInstant(
                    ZoneOffset.UTC), status = journalstatus, type = journalposttype)
            }
        } else emptyList()

        return DetaljertBehandling(
            behandlingId = behandling.behandlingId,
            status = behandling.status,
            behandlingResultater = behandling.behandlingResultater,
            tema = behandling.tema,
            opprettetTidspunkt = behandling.opprettetTidspunkt,
            saksbehandlingsansvarligIdent = behandling.saksbehandlingsansvarligIdent,
            opprettetAv = behandling.opprettetAv,
            sak = behandling.sak,
            dokumenter = dokumenter,
            endretAv = behandling.endretAv,
            behandlendeEnhet = behandling.behandlendeEnhet,
            behandlingsfrist = behandling.behandlingsfrist,
            behandlingstype = behandling.behandlingstype,
            brukerId = behandling.brukerId,
            brukerIdType = behandling.brukerIdType,
            dokumentkategori = behandling.dokumentkategori,
            framdriftsstatus = behandling.framdriftsstatus,
            journalpostId = behandling.journalpostId,
            systemreferanse = behandling.systemreferanse
        )
    }

    fun hentBehandlinger(page: Pageable): Page<BehandlingDto> {
        val behandlingEntities = behandlingRepository.findAll(page)
        return behandlingEntities.map { BehandlingDto.fromEntity(it) }
    }

    fun hentAapneBehandlinger(behandlingsfilter: Behandlingsfilter?, page: Pageable): BehandlingsPage {
        val status = Behandlingsstatus.fromKode(behandlingsfilter?.status.orEmpty())
        val behandlingstype = Behandlingstype.fromKode(behandlingsfilter?.behandlingstype.orEmpty())
        val behandlingEntities = behandlingRepository.findBehandlingerBegrensetTilBehandlingsstatuser(status, behandlingsfilter?.dokumentkategori, behandlingstype, listOf(Behandlingsstatus.UNDER_BEHANDLING, Behandlingsstatus.IKKE_PAABEGYNT), false, page)

        return BehandlingsPage(
            behandlinger = behandlingEntities.content.map { BehandlingDto.fromEntity(it) },
            antallSider = behandlingEntities.totalPages,
            gjeldendeSide = page.pageNumber,
            totaltAntallBehandlinger = behandlingEntities.totalElements
        )
    }

    fun hentAntallBehandlinger(): Long = behandlingRepository.count()

    fun hentEgneBehandlinger(behandlingsstatus: String?, page: Pageable) : BehandlingsPage {
        val status = Behandlingsstatus.valueOfOrNull(behandlingsstatus.orEmpty()) ?: Behandlingsstatus.UNDER_BEHANDLING
        val behandlingEntities = behandlingRepository.findBySaksbehandlingsansvarligIdentAndStatus(autentisertBruker.preferredUsername, status, page)

        return BehandlingsPage(
            behandlinger = behandlingEntities.content.map { BehandlingDto.fromEntity(it)},
            antallSider = behandlingEntities.totalPages,
            gjeldendeSide = page.pageNumber,
            totaltAntallBehandlinger = behandlingEntities.totalElements
        )
    }

    @Transactional
    fun overtaBehandling(behandlingId: Long): BehandlingDto {
        val behandling = hentBehandling(behandlingId)

        // sjekk at behandling ikke allerede tilhører en annen saksbehandler
        if (behandling.saksbehandlingsansvarligIdent != null && behandling.saksbehandlingsansvarligIdent != autentisertBruker.preferredUsername) {
            throw BehandlingException("Behandling tilhører en annen saksbehandler")
        }

        val oppdatertBehandling = behandling.copy(
            status = Behandlingsstatus.UNDER_BEHANDLING,
            saksbehandlingsansvarligIdent = autentisertBruker.preferredUsername,
            endretTidspunkt = Instant.now(),
            endretAv = autentisertBruker.preferredUsername
        )

        return BehandlingDto.fromEntity(lagreBehandling(oppdatertBehandling))
    }

    @Transactional
    fun ferdigstillBehandling(ferdigstillBehandling: FerdigstillBehandling) : FerdigstiltBehandlingDto {
        val behandling = hentBehandling(ferdigstillBehandling.behandlingId)

        // kan kun ferdigstille behandling som har status UNDER_BEHANDLING
        if (behandling.status != Behandlingsstatus.UNDER_BEHANDLING) {
            throw BehandlingException("Kan ikke ferdigstille behandling. Behandling har status ${behandling.status}")
        }

        // sjekk at behandling ikke allerede tilhører en annen saksbehandler
        if (behandling.saksbehandlingsansvarligIdent != autentisertBruker.preferredUsername) {
            throw BehandlingException("Behandling tilhører en annen saksbehandler")
        }

        val oppdatertBehandling = behandling.copy(
            status = Behandlingsstatus.FERDIG,
            saksbehandlingsansvarligIdent = autentisertBruker.preferredUsername,
            endretTidspunkt = Instant.now(),
            endretAv = autentisertBruker.preferredUsername
        )

        val lagretBehandling = lagreBehandling(oppdatertBehandling)
        val lagretBehandlingDto = BehandlingDto.fromEntity(lagretBehandling)
        var ferdigstiltBehandlingDto = FerdigstiltBehandlingDto(lagretBehandlingDto)

        if (lagretBehandling.behandlingstype == Behandlingstype.JOURNALFOERING) {
            dokarkivClient.ferdigstillJournalpost(behandling.journalpostId,
                FerdigstillJournalpostRequest(
                    journalfoerendeEnhet = behandling.behandlendeEnhet!!
                )
            )

            val veiledningsbehandling = opprettVeiledningsbehandling(lagretBehandling)
            ferdigstiltBehandlingDto = FerdigstiltBehandlingDto(lagretBehandlingDto, BehandlingDto.fromEntity(veiledningsbehandling))
        }
        else {
            logger.info("Forsøkte å ferdigstille journalpost ${behandling.journalpostId} med behandlingstype ${behandling.behandlingstype}!")
        }

        return ferdigstiltBehandlingDto
    }

    private fun opprettVeiledningsbehandling(journalfoering: BehandlingEntity): BehandlingEntity {
        val veiledningsbehandling = BehandlingEntity(
            behandlingId = 0,
            tema = journalfoering.tema,
            brukerId = journalfoering.brukerId,
            brukerIdType = journalfoering.brukerIdType,
            behandlendeEnhet = journalfoering.behandlendeEnhet,
            behandlingstype = Behandlingstype.VEILEDNING,
            status = Behandlingsstatus.UNDER_BEHANDLING,
            behandlingsfrist = FristFerdigstillelseTimeManager.nesteGyldigeFristForFerdigstillelseInstant(Behandlingstype.VEILEDNING, Instant.now()),
            journalpostId = journalfoering.journalpostId,
            utgaaendeJournalpostId = null,
            dokumentkategori = journalfoering.dokumentkategori,
            systemreferanse = UUID.randomUUID().toString(),
            framdriftsstatus = Framdriftsstatus.IKKE_PAABEGYNT,
            opprettetTidspunkt = Instant.now(),
            opprettetAv = applicationName,
            behandlingResultater = emptyList(),
            sak = null,
            saksbehandlingsansvarligIdent = journalfoering.saksbehandlingsansvarligIdent
        )
        return lagreBehandling(veiledningsbehandling)
    }

    @Transactional
    fun leggTilbakeBehandling(behandlingId: Long): BehandlingDto {
        val behandling = hentBehandling(behandlingId)

        if (behandling.saksbehandlingsansvarligIdent == null) {
            throw IllegalStateException("${behandling.behandlingId} er ikke tildelt")
        }

        val brukerIdent = autentisertBruker.preferredUsername
        if (behandling.saksbehandlingsansvarligIdent != brukerIdent) {
            throw IllegalStateException("$brukerIdent er ikke saksbehandler for behandling ${behandling.behandlingId}")
        }

        val oppdatertBehandling = behandling.copy(
            saksbehandlingsansvarligIdent = null,
            endretTidspunkt = Instant.now(),
            endretAv = autentisertBruker.preferredUsername
        )

        return BehandlingDto.fromEntity(lagreBehandling(oppdatertBehandling))
    }

    fun hentBehandling(behandlingId: Long): BehandlingEntity {
        return behandlingRepository.findById(behandlingId).orElseThrow()
    }

    /**
     * Overfører behandling som ikke er ferdigstilt til legacy system ved hjelp av Oppgave API
     */
    @Transactional
    fun overforBehandlingTilLegacy(behandlingId: Long, avviksbegrunnelse: String): Boolean {
        val behandling = hentBehandling(behandlingId)

        if (behandling.status == Behandlingsstatus.FERDIG) {
            throw IllegalStateException("Behandling '${behandling.behandlingId}' er allerede ferdigstilt og kan ikke overføres")
        }

        if (behandling.behandlingstype != Behandlingstype.JOURNALFOERING) {
            throw IllegalStateException("Behandling er av type ${behandling.behandlingstype} og kan ikke oversendes. Forventet ${Behandlingstype.JOURNALFOERING}")
        }

        if (behandling.saksbehandlingsansvarligIdent != autentisertBruker.preferredUsername) {
            throw IllegalStateException("Behandling kan kun overføres av ansvarlig saksbehandler")
        }

        val journalpostResult = safClient.hentOppdatertJournalpost(behandling.journalpostId)
        val journalpost = journalpostResult?.journalpost
            ?: throw IllegalStateException("Kunne ikke finne en journalpost for journalpostId: '${behandling.journalpostId} for behandling: '${behandling.behandlingId}'")

        val aktoerId = hentAktoerId(journalpost.bruker)

        val krutkoder = KrutkodeMapping.fromBrevkode(journalpost.hentBrevkode())

        // flytt behandling til legacy
        val journalfoeringOppgave = OpprettJournalfoeringOppgave(
            beskrivelse = "${journalpost.hentHovedDokumentTittel()} - overført fra $applicationShortName",
            journalpostId = journalpost.journalpostId,
            aktoerId = aktoerId,
            tema = journalpost.tema.toString(),
            tildeltEnhetsnr = behandling.behandlendeEnhet,
            oppgavetype = Oppgavetype.JOURNALFOERING.kortnavn,
            behandlingstema = krutkoder.behandlingstema,
            behandlingstype = krutkoder.behandlingstype,
            prioritet = Prioritet.NORM,
            fristFerdigstillelse = FristFerdigstillelseTimeManager
                .nesteGyldigeFristForFerdigstillelseLocalDate(
                    behandling.behandlingstype,
                    journalpost.datoOpprettet.toInstant(
                        ZoneOffset.UTC
                    )
                ),
            aktivDato = journalpost.datoOpprettet.toLocalDate()
        )

        val oppgave = oppgaveClient.opprettOppgave(journalfoeringOppgave)

        // log overføringen
        behandlingsoverfoeringLogService.overfoerBehandling(behandling, oppgave, avviksbegrunnelse)

        // hard slett behandling
        behandlingRepository.delete(behandling)

        val oppdatertBehandling = behandling.copy(
            saksbehandlingsansvarligIdent = null,
            endretTidspunkt = Instant.now(),
            endretAv = autentisertBruker.preferredUsername,
        )
        foerMetrikkIBigQuery(oppdatertBehandling, true)

        return true
    }

    private fun hentAktoerId(bruker: Bruker?): String? {
        if (bruker?.id.isNullOrEmpty()) {
            logger.warn("Journalposten har ingen brukerId.")
            return null
        }
        return when (bruker!!.type) {
            BrukerIdType.AKTOERID -> bruker.id
            BrukerIdType.FNR -> pdlService.hentAktorId(bruker.id!!)
            else -> throw RuntimeException("Ugyldig brukerIdType: ${bruker.type}")
        }
    }

    @Transactional
    fun lagreUtgaaendeJournalpostFraBrevutsending(behandlingId: Long, journalpostId: String) {
        val behandling = hentBehandling(behandlingId)

        if (behandling.utgaaendeJournalpostId != null) {
            throw BehandlingException("Kan ikke ferdigstille behandling. Behandling har allerede utgående journalpostId ${behandling.utgaaendeJournalpostId}")
        }

        val behandlingMedUtgaaendeJournalpostId = behandling.copy(
            utgaaendeJournalpostId = journalpostId,
            endretTidspunkt = Instant.now()
        )

        lagreBehandling(behandlingMedUtgaaendeJournalpostId)
        knyttUtgaaendeJournalpostTilJournalfoeringsbehandling(behandlingMedUtgaaendeJournalpostId.journalpostId, journalpostId)
    }

    private fun knyttUtgaaendeJournalpostTilJournalfoeringsbehandling(
        inngaaendeJournalpostId: String,
        utgaaendeJournalpostId: String
    ) {
        val korresponderendeJournalfoeringsbehandling = behandlingRepository.findByJournalpostIdAndBehandlingstype(
            inngaaendeJournalpostId,
            Behandlingstype.JOURNALFOERING
        ) ?: throw NoSuchElementException("Fant ikke behandling")

        lagreBehandling(
            korresponderendeJournalfoeringsbehandling.copy(
                utgaaendeJournalpostId = utgaaendeJournalpostId,
                endretTidspunkt = Instant.now()
            )
        )
    }

    /**
     * Legger til en rad i BigQuery-metrikkene om en behandling er opprettet/endret.
     *
     * @param behandling behandlingen som ble opprettet
     */
    private fun foerMetrikkIBigQuery(behandling: BehandlingEntity, overfoertLegacy: Boolean = false) {
        val payload = BehandlingPayload(
            behandlingId = behandling.behandlingId.toString(),
            journalpostId = behandling.journalpostId,
            utgaaendeJournalpostId = behandling.utgaaendeJournalpostId,
            dokumentkategori = behandling.dokumentkategori,
            behandlingstype = behandling.behandlingstype.kode,
            behandlingsstatus = behandling.status.kode,
            enhetsnr = behandling.behandlendeEnhet ?: "9999",
            overfoertLegacy = overfoertLegacy,
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