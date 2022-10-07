package no.nav.yrkesskade.saksbehandling.graphql.common.model

import java.time.Instant

data class Tidsfilter(
    val endretSiden: Instant?,
    val opprettetSiden: Instant?
)