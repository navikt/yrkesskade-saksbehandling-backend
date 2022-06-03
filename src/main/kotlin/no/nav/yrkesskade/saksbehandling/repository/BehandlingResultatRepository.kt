package no.nav.yrkesskade.saksbehandling.repository

import no.nav.yrkesskade.saksbehandling.model.BehandlingsresultatEntity
import org.springframework.data.jpa.repository.JpaRepository

interface BehandlingResultatRepository: JpaRepository<BehandlingsresultatEntity, Long> {
}