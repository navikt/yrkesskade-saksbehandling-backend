package no.nav.yrkesskade.saksbehandling.repository

import no.nav.yrkesskade.saksbehandling.model.DokumentMetaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface DokumentMetaRepository : JpaRepository<DokumentMetaEntity, Long> {
}