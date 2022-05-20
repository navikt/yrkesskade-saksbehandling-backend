package no.nav.yrkesskade.saksbehandling.skademelding.repository

import no.nav.yrkesskade.saksbehandling.skademelding.model.SkadetDelEntity
import no.nav.yrkesskade.saksbehandling.skademelding.model.SkadetDelId
import org.springframework.data.jpa.repository.JpaRepository

interface SkadetDelDao  : JpaRepository<SkadetDelEntity, SkadetDelId> {}
