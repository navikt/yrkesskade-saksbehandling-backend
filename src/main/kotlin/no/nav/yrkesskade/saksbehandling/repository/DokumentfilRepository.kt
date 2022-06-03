package no.nav.yrkesskade.saksbehandling.repository

import no.nav.yrkesskade.saksbehandling.model.DokumentfilEntity
import org.springframework.data.jpa.repository.JpaRepository

interface DokumentfilRepository : JpaRepository<DokumentfilEntity, Long> {
}