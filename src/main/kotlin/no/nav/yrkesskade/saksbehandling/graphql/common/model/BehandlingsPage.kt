package no.nav.yrkesskade.saksbehandling.graphql.common.model

import no.nav.yrkesskade.saksbehandling.model.dto.BehandlingDto

data class BehandlingsPage(
    val behandlinger: List<BehandlingDto>,
    val totaltAntallBehandlinger: Long? = null,
    val antallSider: Int? = null,
    val gjeldendeSide: Int? = null,
)