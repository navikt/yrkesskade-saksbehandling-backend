package no.nav.yrkesskade.saksbehandling.service

import DetaljertBehandling
import no.nav.yrkesskade.saksbehandling.graphql.client.ISafClient
import no.nav.yrkesskade.saksbehandling.model.BehandlingEntity
import no.nav.yrkesskade.saksbehandling.model.Behandlingsstatus
import no.nav.yrkesskade.saksbehandling.model.DokumentInfo
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
import java.time.ZoneOffset

@Service
class BehandlingService(
    private val autentisertBruker: AutentisertBruker,
    private val behandlingRepository: BehandlingRepository,
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
            logger.info("Lagret behandling med journalpostId ${behandlingEntity.journalpostId} og behandlingId ${behandlingEntity.behandlingId}")
        }
    }

    fun hentBehandling(behandlingId: Long): DetaljertBehandling {
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

    fun hentBehandlinger(page: Pageable): Page<BehandlingEntity> = behandlingRepository.findAll(page)

    fun hentBehandlingDtos(page: Pageable): Page<BehandlingDto> {
        val behandlingEntities = behandlingRepository.findAll(page)
        val kodeverkHolder = KodeverkHolder.init(kodeverkService = kodeverkService)
        return behandlingEntities.map { BehandlingDto.fromEntity(it, KodeverdiMapper(kodeverkHolder)) }
    }

    fun hentAntallBehandlinger(): Long = behandlingRepository.count()

    fun hentEgneBehandlinger(page: Pageable) : List<BehandlingEntity> {
        return behandlingRepository.findBySaksbehandlingsansvarligIdent(autentisertBruker.preferredUsername, page)
    }

    @Transactional
    fun overtaBehandling(behandlingId: Long): BehandlingEntity {
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

        return behandlingRepository.save(oppdatertBehandling)
    }

    @Transactional
    fun ferdigstillBehandling(behandlingId: Long) : BehandlingEntity {
        val behandling = behandlingRepository.findById(behandlingId).orElseThrow()

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

        return behandlingRepository.save(oppdatertBehandling)
    }

    @Transactional
    fun leggTilbakeBehandling(behandlingId: Long): BehandlingEntity {
        val behandling = behandlingRepository.findById(behandlingId).orElseThrow()

        if (behandling.saksbehandlingsansvarligIdent == null) {
            throw IllegalStateException("${behandling.behandlingId} er ikke tildelt")
        }

        val brukerIdent = autentisertBruker.preferredUsername
        if (!behandling.saksbehandlingsansvarligIdent.equals(brukerIdent)) {
            throw IllegalStateException("$brukerIdent er ikke saksbehandler for behandling ${behandling.behandlingId}")
        }

        val oppdatertBehandling = behandling.copy(status = Behandlingsstatus.IKKE_PAABEGYNT, saksbehandlingsansvarligIdent = null, endretAv = autentisertBruker.preferredUsername)

        return behandlingRepository.save(oppdatertBehandling)
    }
}