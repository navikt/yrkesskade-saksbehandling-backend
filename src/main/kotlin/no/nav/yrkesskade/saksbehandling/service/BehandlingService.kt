package no.nav.yrkesskade.saksbehandling.service

import no.nav.yrkesskade.saksbehandling.model.BehandlingEntity
import no.nav.yrkesskade.saksbehandling.model.Behandlingsstatus
import no.nav.yrkesskade.saksbehandling.repository.BehandlingRepository
import no.nav.yrkesskade.saksbehandling.security.AutentisertBruker
import no.nav.yrkesskade.saksbehandling.util.getLogger
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.IllegalStateException

@Service
class BehandlingService(
    private val autentisertBruker: AutentisertBruker,
    private val behandlingRepository: BehandlingRepository
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

    fun hentBehandlinger(page: Pageable): Page<BehandlingEntity> = behandlingRepository.findAll(page)
    fun hentAntallBehandlinger(): Long = behandlingRepository.count()

    fun hentEgneBehandlinger(page: Pageable) : List<BehandlingEntity> {
        return behandlingRepository.findBySaksbehandlingsansvarligIdent(autentisertBruker.preferredUsername, page)
    }

    @Transactional
    fun overtaBehandling(behandlingId: Long): BehandlingEntity {
        val behandling = behandlingRepository.findById(behandlingId).orElseThrow()
        val oppdatertBehandling = behandling.copy(
            status = Behandlingsstatus.UNDER_BEHANDLING,
            saksbehandlingsansvarligIdent = autentisertBruker.preferredUsername
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

        val oppdatertBehandling = behandling.copy(status = Behandlingsstatus.IKKE_PAABEGYNT, saksbehandlingsansvarligIdent = null)

        return behandlingRepository.save(oppdatertBehandling)
    }
}