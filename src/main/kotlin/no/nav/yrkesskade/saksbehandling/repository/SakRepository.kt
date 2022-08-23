package no.nav.yrkesskade.saksbehandling.repository

import no.nav.yrkesskade.saksbehandling.model.SakEntity
import no.nav.yrkesskade.saksbehandling.model.Saksstatus
import org.springframework.data.jpa.repository.JpaRepository

interface SakRepository : JpaRepository<SakEntity, Long> {

    fun findByBrukerIdentifikator(brukerIdentifikator: String): List<SakEntity>
    fun findByBrukerIdentifikatorAndSaksstatus(brukerIdentifikator: String, saksstatus: Saksstatus): List<SakEntity>
}