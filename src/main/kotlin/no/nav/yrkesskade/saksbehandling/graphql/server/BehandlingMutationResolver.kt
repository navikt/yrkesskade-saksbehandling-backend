package no.nav.yrkesskade.saksbehandling.graphql.server

import graphql.kickstart.tools.GraphQLMutationResolver
import no.nav.yrkesskade.saksbehandling.graphql.common.model.FerdigstillBehandling
import no.nav.yrkesskade.saksbehandling.model.BehandlingEntity
import no.nav.yrkesskade.saksbehandling.model.dto.BehandlingDto
import no.nav.yrkesskade.saksbehandling.model.dto.FerdigstiltBehandlingDto
import no.nav.yrkesskade.saksbehandling.service.BehandlingService
import org.springframework.stereotype.Component

@Component
class BehandlingMutationResolver(
    private val behandlingService: BehandlingService
) : GraphQLMutationResolver {

    fun overtaBehandling(behandlingId: Long) : BehandlingDto = behandlingService.overtaBehandling(behandlingId)

    fun ferdigstillBehandling(ferdigstillBehandling: FerdigstillBehandling) : FerdigstiltBehandlingDto = behandlingService.ferdigstillBehandling(ferdigstillBehandling)
    fun leggTilbakeBehandling(behandlingId: Long) : BehandlingDto = behandlingService.leggTilbakeBehandling(behandlingId)

    fun overforBehandlingTilLegacy(behandlingId: Long, avviksgrunn: String): Boolean = behandlingService.overforBehandlingTilLegacy(behandlingId, avviksgrunn)
}