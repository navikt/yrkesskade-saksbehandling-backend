package no.nav.yrkesskade.saksbehandling.repository

import no.nav.yrkesskade.saksbehandling.model.DokumentFilEntity
import org.springframework.data.jpa.repository.JpaRepository

interface DokumentfilRepository : JpaRepository<DokumentFilEntity, Long> {
}