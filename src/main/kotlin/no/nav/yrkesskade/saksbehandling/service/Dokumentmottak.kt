package no.nav.yrkesskade.saksbehandling.service

import com.expediagroup.graphql.generated.enums.BrukerIdType
import com.expediagroup.graphql.generated.journalpost.Journalpost
import no.nav.yrkesskade.saksbehandling.client.BrevutsendingClient
import no.nav.yrkesskade.saksbehandling.graphql.client.SafClient
import no.nav.yrkesskade.saksbehandling.model.BehandlingEntity
import no.nav.yrkesskade.saksbehandling.model.Behandlingsstatus
import no.nav.yrkesskade.saksbehandling.model.Behandlingstype
import no.nav.yrkesskade.saksbehandling.model.Brev
import no.nav.yrkesskade.saksbehandling.model.BrevutsendingBestiltHendelse
import no.nav.yrkesskade.saksbehandling.model.BrevutsendingMetadata
import no.nav.yrkesskade.saksbehandling.model.DokumentTilSaksbehandling
import no.nav.yrkesskade.saksbehandling.model.DokumentTilSaksbehandlingHendelse
import no.nav.yrkesskade.saksbehandling.model.Framdriftsstatus
import no.nav.yrkesskade.saksbehandling.model.pdf.PdfData
import no.nav.yrkesskade.saksbehandling.model.pdf.PdfTemplate
import no.nav.yrkesskade.saksbehandling.util.MDCConstants
import no.nav.yrkesskade.saksbehandling.util.getLogger
import no.nav.yrkesskade.saksbehandling.util.getSecureLogger
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

@Component
class Dokumentmottak(
    private val behandlingService: BehandlingService,
    private val sakService: SakService,
    private val safClient: SafClient,
    private val brevutsendingClient: BrevutsendingClient
) {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    @Transactional(propagation = Propagation.REQUIRED)
    fun mottaDokument(dokumentTilSaksbehandlingHendelse: DokumentTilSaksbehandlingHendelse) {
        // hente JP i SAF
        val dokumentTilSaksbehandling = dokumentTilSaksbehandlingHendelse.dokumentTilSaksbehandling
        val journalpost = hentJournalpostFraSaf(dokumentTilSaksbehandling.journalpostId)

        // lete etter SakEntity basert på brukerId, evt. fagsakId

//        val eksisterendeSak = sakService.hentSak(journalpost.bruker!!.id!!)

        val behandling: BehandlingEntity = BehandlingEntity(
            behandlingId = 0,
            tema = journalpost.tema!!.name,
            brukerId = journalpost.bruker!!.id!!,
            behandlendeEnhet = dokumentTilSaksbehandling.enhet,
            behandlingstype = Behandlingstype.VEILEDNING,
            status = Behandlingsstatus.IKKE_PAABEGYNT,
            behandlingsfrist = Instant.now().plus(2, ChronoUnit.DAYS),
            journalpostId = dokumentTilSaksbehandling.journalpostId,
            systemreferanse = UUID.randomUUID().toString(),
            framdriftsstatus = Framdriftsstatus.IKKE_PAABEGYNT,
            opprettetTidspunkt = Instant.now(),
            opprettetAv = "yrkesskade-saksbehandling-backend",
            behandlingResultater = emptyList(),
            sak = null,
        )
        behandlingService.lagreBehandling(behandling)

        sendTilBrevutsending(dokumentTilSaksbehandling)
    }

    private fun sendTilBrevutsending(dokumentTilSaksbehandling: DokumentTilSaksbehandling) {
        val pdfData = PdfData(
            brevtype = "Veiledning",
            uuid = UUID.randomUUID().toString()
        )
        brevutsendingClient.sendTilBrevutsending(
            BrevutsendingBestiltHendelse(
                brev = Brev(
                    tittel = "Veiledningsbrev tannlegeerklæring",
                    brevkode = "NAV 13-00.08", // avklare? fjerne?
                    enhet = dokumentTilSaksbehandling.enhet,
                    template = PdfTemplate.TANNLEGEERKLAERING_VEILEDNING,
                    innhold = pdfData
                ),
                metadata = BrevutsendingMetadata(
                    innkommendeJournalpostId = dokumentTilSaksbehandling.journalpostId,
                    tidspunktBestilt = Instant.now(),
                    navCallId = MDC.get(MDCConstants.MDC_CALL_ID)
                )
            )
        )
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
}
