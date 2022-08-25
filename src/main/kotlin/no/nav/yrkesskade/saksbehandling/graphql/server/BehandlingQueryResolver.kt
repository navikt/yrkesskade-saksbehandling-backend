package no.nav.yrkesskade.saksbehandling.graphql.server

import DetaljertBehandling
import graphql.kickstart.tools.GraphQLQueryResolver
import no.nav.yrkesskade.saksbehandling.graphql.common.model.Page
import no.nav.yrkesskade.saksbehandling.model.BehandlingEntity
import no.nav.yrkesskade.saksbehandling.service.BehandlingService
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class BehandlingQueryResolver(
    private val behandlingService: BehandlingService
    ) : GraphQLQueryResolver {

    fun hentBehandlinger(page: Page) = behandlingService.hentBehandlinger(PageRequest.of(page.page, page.size))

    fun hentEgneBehandlinger(page: Page): List<BehandlingEntity> {
        return behandlingService.hentEgneBehandlinger(PageRequest.of(page.page, page.size))
    }

    fun hentBehandling(behandlingId: Long) : DetaljertBehandling {
        return behandlingService.hentBehandling(behandlingId)
    }

    fun antallBehandlinger() = behandlingService.hentAntallBehandlinger()
}