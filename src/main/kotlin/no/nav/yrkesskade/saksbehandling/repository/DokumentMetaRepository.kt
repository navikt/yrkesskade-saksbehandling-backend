package no.nav.yrkesskade.saksbehandling.repository

import no.nav.yrkesskade.saksbehandling.model.DokumentEntity
import org.springframework.data.jpa.repository.JpaRepository

interface DokumentMetaRepository : JpaRepository<DokumentEntity, Long> {
}