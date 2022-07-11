package no.nav.yrkesskade.saksbehandling.graphql.server

import graphql.kickstart.tools.GraphQLQueryResolver
import no.nav.yrkesskade.saksbehandling.graphql.common.model.Page
import no.nav.yrkesskade.saksbehandling.repository.BehandlingRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class BehandlingQueryResolver(private val behandlingRepository: BehandlingRepository) : GraphQLQueryResolver {

    fun hentBehandlinger(page: Page) =
        behandlingRepository.findAll(PageRequest.of(page.page, page.size))
    fun antallBehandlinger() = behandlingRepository.count()
}