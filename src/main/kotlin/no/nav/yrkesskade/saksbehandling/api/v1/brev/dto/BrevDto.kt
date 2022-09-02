package no.nav.yrkesskade.saksbehandling.api.v1.brev.dto

import no.nav.yrkesskade.saksbehandling.model.Brev

data class BrevDto(
    val innkommendeJournalpostId: String,
    val brev: Brev
)