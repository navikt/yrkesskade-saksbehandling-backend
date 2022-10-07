package no.nav.yrkesskade.saksbehandling.model

import java.time.Instant

data class DokumentInfo(
    val dokumentinfoId: String,
    val journalpostId: String,
    val tittel: String,
    val type: String,
    val opprettetTidspunkt: Instant,
    val status: String
)