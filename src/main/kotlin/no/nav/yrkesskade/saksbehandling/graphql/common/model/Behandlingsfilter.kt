package no.nav.yrkesskade.saksbehandling.graphql.common.model

data class Behandlingsfilter(
    val behandlingstype: String?,
    val dokumentkategori: String?,
    val status: String?
)