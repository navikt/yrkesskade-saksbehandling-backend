package no.nav.yrkesskade.saksbehandling.repository

import no.nav.yrkesskade.saksbehandling.model.BehandlingEntity
import no.nav.yrkesskade.saksbehandling.model.Behandlingsstatus
import no.nav.yrkesskade.saksbehandling.model.Behandlingstype
import no.nav.yrkesskade.saksbehandling.model.SakEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BehandlingRepository : JpaRepository<BehandlingEntity, Long> {

    fun findBySak(sak: SakEntity): List<BehandlingEntity>

    fun findByBehandlingId(behandlingId: Long): BehandlingEntity?

    @Query("""
        SELECT b FROM BehandlingEntity b
        WHERE b.status = no.nav.yrkesskade.saksbehandling.model.Behandlingsstatus.UNDER_BEHANDLING
        AND b.saksbehandlingsansvarligIdent = :ident
    """)
    fun findBySaksbehandlingsansvarligIdent(@Param("ident") ident: String, pageable: Pageable): List<BehandlingEntity>

    @Query(
        """
            SELECT b FROM BehandlingEntity b
            WHERE (b.status = :status OR CAST(:status as no.nav.yrkesskade.saksbehandling.model.Behandlingsstatus) IS NULL)
            AND (b.dokumentkategori = :dokumentkategori OR CAST(:dokumentkategori as java.lang.String) IS NULL)
            AND (b.behandlingstype = :behandlingstype OR CAST(:behandlingstype as no.nav.yrkesskade.saksbehandling.model.Behandlingstype) IS NULL)
            AND b.status IN (:statuser)
             """
    )
    fun findBehandlingerBegrensetTilBehandlingsstatuser(
        @Param("status") status: Behandlingsstatus?,
        @Param("dokumentkategori") dokumentkategori: String?,
        @Param("behandlingstype") behandlingstype: Behandlingstype?,
        @Param("statuser") statuser: List<Behandlingsstatus>,
        pageable: Pageable
    ): Page<BehandlingEntity>

}