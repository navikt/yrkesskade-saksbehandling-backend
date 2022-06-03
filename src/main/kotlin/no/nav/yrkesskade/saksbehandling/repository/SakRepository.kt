package no.nav.yrkesskade.saksbehandling.repository

import no.nav.yrkesskade.saksbehandling.model.SakEntity
import org.springframework.data.jpa.repository.JpaRepository

interface SakRepository : JpaRepository<SakEntity, Long> {

    fun findByBrukerIdentifikator(brukerIdentifikator: String): List<SakEntity>
}