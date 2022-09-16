package no.nav.yrkesskade.saksbehandling.graphql.server

import DetaljertBehandling
import graphql.kickstart.tools.GraphQLQueryResolver
import no.nav.yrkesskade.saksbehandling.graphql.common.model.Behandlingsfilter
import no.nav.yrkesskade.saksbehandling.graphql.common.model.Page
import no.nav.yrkesskade.saksbehandling.service.BehandlingService
import org.springframework.stereotype.Component

@Component
class BehandlingQueryResolver(
    private val behandlingService: BehandlingService
    ) : GraphQLQueryResolver {

    fun hentBehandlinger(page: Page) = behandlingService.hentBehandlinger(page.tilPageRequest())

    fun hentAapneBehandlinger(behandlingsfilter: Behandlingsfilter?, page: Page) = behandlingService.hentAapneBehandlinger(behandlingsfilter, page.tilPageRequest())

    fun hentEgneBehandlinger(behandlingsstatus: String?, page: Page) = behandlingService.hentEgneBehandlinger(page = page.tilPageRequest(), behandlingsstatus = behandlingsstatus)

    fun hentBehandling(behandlingId: Long) : DetaljertBehandling {
        return behandlingService.hentDetaljertBehandling(behandlingId)
    }

    fun antallBehandlinger() = behandlingService.hentAntallBehandlinger()
}