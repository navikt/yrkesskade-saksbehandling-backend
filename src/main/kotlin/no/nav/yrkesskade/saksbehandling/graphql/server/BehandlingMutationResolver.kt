package no.nav.yrkesskade.saksbehandling.graphql.server

import graphql.kickstart.tools.GraphQLMutationResolver
import no.nav.yrkesskade.saksbehandling.model.BehandlingEntity
import no.nav.yrkesskade.saksbehandling.service.BehandlingService
import org.springframework.stereotype.Component

@Component
class BehandlingMutationResolver(
    private val behandlingService: BehandlingService
) : GraphQLMutationResolver {

    fun overtaBehandling(behandlingId: Long) : BehandlingEntity = behandlingService.overtaBehandling(behandlingId)

    fun ferdigstillBehandling(behandlingId: Long) : BehandlingEntity = behandlingService.ferdigstillBehandling(behandlingId)
    fun leggTilbakeBehandling(behandlingId: Long) : BehandlingEntity = behandlingService.leggTilbakeBehandling(behandlingId)
}