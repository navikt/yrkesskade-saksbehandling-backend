package no.nav.yrkesskade.saksbehandling.model.dto

data class FerdigstiltBehandlingDto(
    val behandling: BehandlingDto,
    val nesteBehandling: BehandlingDto? = null
)
