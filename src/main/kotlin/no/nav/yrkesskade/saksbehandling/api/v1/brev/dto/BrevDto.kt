package no.nav.yrkesskade.saksbehandling.api.v1.brev.dto

import no.nav.yrkesskade.saksbehandling.model.pdf.PdfTemplate

data class BrevDto(
    val innkommendeJournalpostId: String,
    val brevtype: String,
    val tittel: String,
    val brevkode: String,
    val enhet: String,
    val template: PdfTemplate,
    val innhold: String
)