package no.nav.yrkesskade.saksbehandling.skademelding.repository

import no.nav.yrkesskade.saksbehandling.skademelding.model.SkadelidtStillingEntity
import no.nav.yrkesskade.saksbehandling.skademelding.model.SkadelidtStillingId
import org.springframework.data.jpa.repository.JpaRepository

interface StillingstittelDao: JpaRepository<SkadelidtStillingEntity, SkadelidtStillingId> {}