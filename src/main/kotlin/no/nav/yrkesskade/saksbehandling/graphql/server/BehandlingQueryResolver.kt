package no.nav.yrkesskade.saksbehandling.graphql.server

import DetaljertBehandling
import graphql.kickstart.tools.GraphQLQueryResolver
import no.nav.yrkesskade.saksbehandling.graphql.common.PageRequestBuilder
import no.nav.yrkesskade.saksbehandling.graphql.common.model.BehandlingsPage
import no.nav.yrkesskade.saksbehandling.graphql.common.model.Behandlingsfilter
import no.nav.yrkesskade.saksbehandling.graphql.common.model.Page
import no.nav.yrkesskade.saksbehandling.graphql.common.model.sorting.BehandlingSortFieldType
import no.nav.yrkesskade.saksbehandling.graphql.common.model.sorting.BehandlingSortering
import no.nav.yrkesskade.saksbehandling.model.dto.BehandlingDto
import no.nav.yrkesskade.saksbehandling.service.BehandlingService
import org.springframework.stereotype.Component
import org.springframework.data.domain.Page as JpaPage

@Component
class BehandlingQueryResolver(
    private val behandlingService: BehandlingService
    ) : GraphQLQueryResolver {

    fun hentBehandlinger(page: Page, behandlingSortering: BehandlingSortering?): JpaPage<BehandlingDto> {
        val pageRequestBuilder = PageRequestBuilder(page, behandlingSortering)
        return behandlingService.hentBehandlinger(pageRequestBuilder.build<BehandlingSortFieldType>())
    }

    fun hentAapneBehandlinger(behandlingsfilter: Behandlingsfilter?, page: Page, behandlingSortering: BehandlingSortering?): BehandlingsPage {
        val pageRequestBuilder = PageRequestBuilder(page, behandlingSortering)
        return behandlingService.hentAapneBehandlinger(behandlingsfilter, pageRequestBuilder.build<BehandlingSortFieldType>())
    }

    fun hentEgneBehandlinger(behandlingsstatus: String?, page: Page, behandlingSortering: BehandlingSortering?): BehandlingsPage {
        val pageRequestBuilder = PageRequestBuilder(page, behandlingSortering)
        return behandlingService.hentEgneBehandlinger(page = pageRequestBuilder.build<BehandlingSortFieldType>(), behandlingsstatus = behandlingsstatus)
    }

    fun hentBehandling(behandlingId: Long) : DetaljertBehandling {
        return behandlingService.hentDetaljertBehandling(behandlingId)
    }

    fun antallBehandlinger() = behandlingService.hentAntallBehandlinger()
}