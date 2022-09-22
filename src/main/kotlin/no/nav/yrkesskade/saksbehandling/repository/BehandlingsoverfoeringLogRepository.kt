package no.nav.yrkesskade.saksbehandling.repository

import no.nav.yrkesskade.saksbehandling.model.BehandlingsoverfoeringLogEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BehandlingsoverfoeringLogRepository : JpaRepository<BehandlingsoverfoeringLogEntity, Long> {}