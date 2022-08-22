package no.nav.yrkesskade.saksbehandling.graphql.server

import graphql.kickstart.tools.GraphQLQueryResolver
import no.nav.yrkesskade.saksbehandling.graphql.common.model.Page
import no.nav.yrkesskade.saksbehandling.model.BehandlingEntity
import no.nav.yrkesskade.saksbehandling.repository.BehandlingRepository
import no.nav.yrkesskade.saksbehandling.security.AutentisertBruker
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class BehandlingQueryResolver(
    private val autentisertBruker: AutentisertBruker,
    private val behandlingRepository: BehandlingRepository
    ) : GraphQLQueryResolver {

    fun hentBehandlinger(page: Page) =
        behandlingRepository.findAll(PageRequest.of(page.page, page.size))

    fun hentEgneBehandlinger(page: Page): List<BehandlingEntity> {
        return behandlingRepository.findByBehandlingsansvarligIdent(autentisertBruker.preferredUsername, PageRequest.of(page.page, page.size))
    }

    fun antallBehandlinger() = behandlingRepository.count()
}