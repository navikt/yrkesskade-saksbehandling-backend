package no.nav.yrkesskade.saksbehandling.service

import DetaljertBehandling
import no.nav.yrkesskade.saksbehandling.client.dokarkiv.DokarkivClient
import no.nav.yrkesskade.saksbehandling.client.dokarkiv.FerdigstillJournalpostRequest
import no.nav.yrkesskade.saksbehandling.graphql.client.saf.ISafClient
import no.nav.yrkesskade.saksbehandling.graphql.common.model.BehandlingsPage
import no.nav.yrkesskade.saksbehandling.graphql.common.model.Behandlingsfilter
import no.nav.yrkesskade.saksbehandling.graphql.common.model.FerdigstillBehandling
import no.nav.yrkesskade.saksbehandling.model.*
import no.nav.yrkesskade.saksbehandling.model.dto.BehandlingDto
import no.nav.yrkesskade.saksbehandling.repository.BehandlingRepository
import no.nav.yrkesskade.saksbehandling.security.AutentisertBruker
import no.nav.yrkesskade.saksbehandling.util.getLogger
import no.nav.yrkesskade.saksbehandling.util.kodeverk.KodeverdiMapper
import no.nav.yrkesskade.saksbehandling.util.kodeverk.KodeverkHolder
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.*

@Service
class BehandlingService(
    private val autentisertBruker: AutentisertBruker,
    private val behandlingRepository: BehandlingRepository,
    private val dokarkivClient: DokarkivClient,
    @Qualifier("safClient") private val safClient: ISafClient,
    private val kodeverkService: KodeverkService
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @Transactional
    fun lagreBehandling(behandlingEntity: BehandlingEntity) {
        behandlingRepository.save(behandlingEntity).also {
            logger.info("Lagret behandling (${behandlingEntity.behandlingstype.name}) med journalpostId ${behandlingEntity.journalpostId} og behandlingId ${behandlingEntity.behandlingId}")
        }
    }

    fun hentDetaljertBehandling(behandlingId: Long): DetaljertBehandling {
        val behandling = behandlingRepository.findById(behandlingId).orElseThrow()

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
        val kodeverkHolder = KodeverkHolder.init(kodeverkService = kodeverkService)
        return behandlingEntities.map { BehandlingDto.fromEntity(it, KodeverdiMapper(kodeverkHolder)) }
    }

    fun hentAapneBehandlinger(behandlingsfilter: Behandlingsfilter?, page: Pageable): BehandlingsPage {
        val status = Behandlingsstatus.fromKode(behandlingsfilter?.status.orEmpty())
        val behandlingstype = Behandlingstype.fromKode(behandlingsfilter?.behandlingstype.orEmpty())
        val behandlingEntities = behandlingRepository.findBehandlingerBegrensetTilBehandlingsstatuser(status, behandlingsfilter?.dokumentkategori, behandlingstype, listOf(Behandlingsstatus.UNDER_BEHANDLING, Behandlingsstatus.IKKE_PAABEGYNT), false, page)
        val kodeverkHolder = KodeverkHolder.init(kodeverkService = kodeverkService)

        return BehandlingsPage(
            behandlinger = behandlingEntities.content.map { BehandlingDto.fromEntity(it, KodeverdiMapper(kodeverkHolder)) },
            antallSider = behandlingEntities.totalPages,
            gjeldendeSide = page.pageNumber,
            totaltAntallBehandlinger = behandlingEntities.totalElements
        )
    }

    fun hentAntallBehandlinger(): Long = behandlingRepository.count()

    fun hentEgneBehandlinger(behandlingsstatus: String?, page: Pageable) : BehandlingsPage {
        val status = Behandlingsstatus.valueOfOrNull(behandlingsstatus.orEmpty()) ?: Behandlingsstatus.UNDER_BEHANDLING
        val behandlingEntities = behandlingRepository.findBySaksbehandlingsansvarligIdentAndStatus(autentisertBruker.preferredUsername, status, page)
        val kodeverkHolder = KodeverkHolder.init(kodeverkService = kodeverkService)

        return BehandlingsPage(
            behandlinger = behandlingEntities.content.map { BehandlingDto.fromEntity(it, KodeverdiMapper(kodeverkHolder)) },
            antallSider = behandlingEntities.totalPages,
            gjeldendeSide = page.pageNumber,
            totaltAntallBehandlinger = behandlingEntities.totalElements
        )
    }

    @Transactional
    fun overtaBehandling(behandlingId: Long): BehandlingDto {
        val behandling = behandlingRepository.findById(behandlingId).orElseThrow()

        // sjekk at behandling ikke allerede tilhører en annen saksbehandler
        if (behandling.saksbehandlingsansvarligIdent != null && behandling.saksbehandlingsansvarligIdent != autentisertBruker.preferredUsername) {
            throw BehandlingException("Behandling tilhører en annen saksbehandler")
        }

        val oppdatertBehandling = behandling.copy(
            status = Behandlingsstatus.UNDER_BEHANDLING,
            saksbehandlingsansvarligIdent = autentisertBruker.preferredUsername,
            endretAv = autentisertBruker.preferredUsername
        )

        val kodeverkHolder = KodeverkHolder.init(kodeverkService = kodeverkService)
        return BehandlingDto.fromEntity(behandlingRepository.save(oppdatertBehandling), KodeverdiMapper(kodeverkHolder))
    }

    @Transactional
    fun ferdigstillBehandling(ferdigstillBehandling: FerdigstillBehandling) : BehandlingDto {
        val behandling = behandlingRepository.findById(ferdigstillBehandling.behandlingId).orElseThrow()

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
            endretAv = autentisertBruker.preferredUsername
        )

        val kodeverkHolder = KodeverkHolder.init(kodeverkService = kodeverkService)
        val lagretBehandling = behandlingRepository.save(oppdatertBehandling)
        val lagretBehandlingDto = BehandlingDto.fromEntity(lagretBehandling, KodeverdiMapper(kodeverkHolder))

        if (lagretBehandling.behandlingstype == Behandlingstype.JOURNALFOERING) {
            dokarkivClient.ferdigstillJournalpost(behandling.journalpostId,
                FerdigstillJournalpostRequest(
                    journalfoerendeEnhet = behandling.behandlendeEnhet!!
                )
            )

            opprettVeiledningsbehandling(lagretBehandling)
        }
        else {
            logger.info("Forsøkte å ferdigstille journalpost ${behandling.journalpostId} med behandlingstype ${behandling.behandlingstype}!")
        }

        return lagretBehandlingDto
    }

    private fun opprettVeiledningsbehandling(journalfoering: BehandlingEntity) {
        val veiledningsbehandling = BehandlingEntity(
            behandlingId = 0,
            tema = journalfoering.tema,
            brukerId = journalfoering.brukerId,
            brukerIdType = journalfoering.brukerIdType,
            behandlendeEnhet = journalfoering.behandlendeEnhet,
            behandlingstype = Behandlingstype.VEILEDNING,
            status = Behandlingsstatus.IKKE_PAABEGYNT,
            behandlingsfrist = Instant.now().plus(30, ChronoUnit.DAYS),
            journalpostId = journalfoering.journalpostId,
            utgaaendeJournalpostId = null,
            dokumentkategori = journalfoering.dokumentkategori,
            systemreferanse = UUID.randomUUID().toString(),
            framdriftsstatus = Framdriftsstatus.IKKE_PAABEGYNT,
            opprettetTidspunkt = Instant.now(),
            opprettetAv = "yrkesskade-saksbehandling-backend",
            behandlingResultater = emptyList(),
            sak = null,
        )
        lagreBehandling(veiledningsbehandling)
    }

    @Transactional
    fun leggTilbakeBehandling(behandlingId: Long): BehandlingDto {
        val behandling = behandlingRepository.findById(behandlingId).orElseThrow()

        if (behandling.saksbehandlingsansvarligIdent == null) {
            throw IllegalStateException("${behandling.behandlingId} er ikke tildelt")
        }

        val brukerIdent = autentisertBruker.preferredUsername
        if (!behandling.saksbehandlingsansvarligIdent.equals(brukerIdent)) {
            throw IllegalStateException("$brukerIdent er ikke saksbehandler for behandling ${behandling.behandlingId}")
        }

        val oppdatertBehandling = behandling.copy(saksbehandlingsansvarligIdent = null, endretAv = autentisertBruker.preferredUsername)

        val kodeverkHolder = KodeverkHolder.init(kodeverkService = kodeverkService)
        return BehandlingDto.fromEntity(behandlingRepository.save(oppdatertBehandling), KodeverdiMapper(kodeverkHolder))
    }

    fun hentBehandling(behandlingId: Long): BehandlingEntity {
        return behandlingRepository.findById(behandlingId).orElseThrow()
    }
}