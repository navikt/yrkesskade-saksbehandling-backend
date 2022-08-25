package no.nav.yrkesskade.saksbehandling.model

import java.time.LocalDateTime

data class DokumentInfo(
    val dokumentinfoId: String,
    val tittel: String,
    val type: String,
    val opprettetTidspunkt: LocalDateTime,
    val status: String
)