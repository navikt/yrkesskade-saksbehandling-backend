package no.nav.yrkesskade.saksbehandling.graphql.common.model

data class BehandlingsPage(
    val page: Page,
    val behandlingsfilter: Behandlingsfilter?
)