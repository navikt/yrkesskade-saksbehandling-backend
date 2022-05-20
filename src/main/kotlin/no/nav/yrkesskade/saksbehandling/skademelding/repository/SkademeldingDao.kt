package no.nav.yrkesskade.saksbehandling.skademelding.repository

import no.nav.yrkesskade.saksbehandling.skademelding.model.SkademeldingEntity
import org.springframework.data.jpa.repository.JpaRepository

interface SkademeldingDao  : JpaRepository<SkademeldingEntity, Long> {

    fun findBySkadelidtIdentitetsnummer(skadelidtIdentitetsnummer: String): List<SkademeldingEntity>
}