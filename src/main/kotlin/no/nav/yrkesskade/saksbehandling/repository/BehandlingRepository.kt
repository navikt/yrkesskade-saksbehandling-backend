package no.nav.yrkesskade.saksbehandling.repository

import no.nav.yrkesskade.saksbehandling.model.BehandlingEntity
import no.nav.yrkesskade.saksbehandling.model.SakEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface BehandlingRepository : JpaRepository<BehandlingEntity, Long> {

    fun findBySak(sak: SakEntity): List<BehandlingEntity>

    fun findByOppgaveId(oppgaveId: String): BehandlingEntity?

    fun findByBehandlingsansvarligIdent(ident: String, pageable: Pageable): List<BehandlingEntity>
}