package no.nav.yrkesskade.saksbehandling.service

import no.nav.yrkesskade.saksbehandling.model.BehandlingEntity
import no.nav.yrkesskade.saksbehandling.model.Behandlingsstatus
import no.nav.yrkesskade.saksbehandling.repository.BehandlingRepository
import org.springframework.stereotype.Service

@Service
class BehandlingService(
    private val behandlingRepository: BehandlingRepository
) {

    fun overtaBehandling(behandlingId: Long): BehandlingEntity {
        val behandling = behandlingRepository.findById(behandlingId).orElseThrow()

        behandling.also {
            it.status = Behandlingsstatus.UNDER_BEHANDLING
            it.behandlingsansvarligIdent = "meg" // bytt ut med brukerens ident
            behandlingRepository.save(it)
        }

        return behandling
    }
}