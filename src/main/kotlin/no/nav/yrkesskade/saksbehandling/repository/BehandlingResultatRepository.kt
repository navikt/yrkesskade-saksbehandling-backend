package no.nav.yrkesskade.saksbehandling.repository

import no.nav.yrkesskade.saksbehandling.model.BehandlingResultatEntity
import org.springframework.data.jpa.repository.JpaRepository

interface BehandlingResultatRepository: JpaRepository<BehandlingResultatEntity, Long> {
}