package no.nav.yrkesskade.saksbehandling.graphql.common.model

data class FerdigstillBehandling(
    val behandlingId: Long,
    val journalfoeringdetaljer: Journalfoeringdetaljer? = null
    )
