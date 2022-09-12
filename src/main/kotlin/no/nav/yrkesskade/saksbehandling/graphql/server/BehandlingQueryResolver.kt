package no.nav.yrkesskade.saksbehandling.graphql.server

import DetaljertBehandling
import graphql.kickstart.tools.GraphQLQueryResolver
import no.nav.yrkesskade.saksbehandling.graphql.common.model.BehandlingsPage
import no.nav.yrkesskade.saksbehandling.graphql.common.model.MinBehandlingsPage
import no.nav.yrkesskade.saksbehandling.graphql.common.model.Page
import no.nav.yrkesskade.saksbehandling.model.BehandlingEntity
import no.nav.yrkesskade.saksbehandling.model.dto.BehandlingDto
import no.nav.yrkesskade.saksbehandling.service.BehandlingService
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class BehandlingQueryResolver(
    private val behandlingService: BehandlingService
    ) : GraphQLQueryResolver {

    fun hentBehandlinger(page: Page) = behandlingService.hentBehandlinger(PageRequest.of(page.page, page.size))

    fun hentAapneBehandlinger(behandlingsPage: BehandlingsPage) = behandlingService.hentAapneBehandlinger(behandlingsPage)

    fun hentEgneBehandlinger(behandlingsPage: MinBehandlingsPage) = behandlingService.hentEgneBehandlinger(behandlingsPage)

    fun hentBehandling(behandlingId: Long) : DetaljertBehandling {
        return behandlingService.hentBehandling(behandlingId)
    }

    fun antallBehandlinger() = behandlingService.hentAntallBehandlinger()
}