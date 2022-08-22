package no.nav.yrkesskade.saksbehandling.service

import no.nav.yrkesskade.saksbehandling.model.BehandlingEntity
import no.nav.yrkesskade.saksbehandling.model.Behandlingsstatus
import no.nav.yrkesskade.saksbehandling.repository.BehandlingRepository
import no.nav.yrkesskade.saksbehandling.security.AutentisertBruker
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class BehandlingService(
    private val autentisertBruker: AutentisertBruker,
    private val behandlingRepository: BehandlingRepository
) {

    fun hentBehandlinger(page: Pageable): Page<BehandlingEntity> = behandlingRepository.findAll(page)
    fun hentAntallBehandlinger(): Long = behandlingRepository.count()

    fun hentEgneBehandlinger(page: Pageable) : List<BehandlingEntity> {
        return behandlingRepository.findByBehandlingsansvarligIdent(autentisertBruker.preferredUsername, page)
    }
    fun overtaBehandling(behandlingId: Long): BehandlingEntity {
        val behandling = behandlingRepository.findById(behandlingId).orElseThrow()

        behandling.also {
            it.status = Behandlingsstatus.UNDER_BEHANDLING
            it.behandlingsansvarligIdent = autentisertBruker.preferredUsername // bytt ut med brukerens ident
            behandlingRepository.save(it)
        }

        return behandling
    }
}