package no.nav.yrkesskade.saksbehandling.repository

import no.nav.yrkesskade.saksbehandling.model.BehandlingEntity
import no.nav.yrkesskade.saksbehandling.model.SakEntity
import org.javers.spring.annotation.JaversSpringDataAuditable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

@JaversSpringDataAuditable
interface BehandlingRepository : JpaRepository<BehandlingEntity, Long> {

    fun findBySak(sak: SakEntity): List<BehandlingEntity>

    fun findByBehandlingId(behandlingId: Long): BehandlingEntity?

    fun findBySaksbehandlingsansvarligIdent(ident: String, pageable: Pageable): List<BehandlingEntity>
}