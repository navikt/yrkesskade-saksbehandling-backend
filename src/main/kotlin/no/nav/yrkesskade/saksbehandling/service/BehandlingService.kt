package no.nav.yrkesskade.saksbehandling.service

import no.nav.yrkesskade.saksbehandling.model.BehandlingEntity
import no.nav.yrkesskade.saksbehandling.repository.BehandlingRepository
import no.nav.yrkesskade.saksbehandling.util.getLogger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BehandlingService(private val behandlingRepository: BehandlingRepository) {

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
}