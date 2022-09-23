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
import org.springframework.stereotype.Repository

interface BehandlingRepository : JpaRepository<BehandlingEntity, Long> {

    fun findBySak(sak: SakEntity): List<BehandlingEntity>

    @Query("""
        SELECT b FROM BehandlingEntity b
        WHERE b.status = :status
        AND b.saksbehandlingsansvarligIdent = :ident
    """)
    fun findBySaksbehandlingsansvarligIdentAndStatus(@Param("ident") ident: String, @Param("status") status: Behandlingsstatus, pageable: Pageable): Page<BehandlingEntity>

    @Query(
        """
            SELECT b FROM BehandlingEntity b
            WHERE (b.status = :status OR CAST(:status as no.nav.yrkesskade.saksbehandling.model.Behandlingsstatus) IS NULL)
            AND (b.dokumentkategori = :dokumentkategori OR CAST(:dokumentkategori as java.lang.String) IS NULL)
            AND (b.behandlingstype = :behandlingstype OR CAST(:behandlingstype as no.nav.yrkesskade.saksbehandling.model.Behandlingstype) IS NULL)
            AND b.status IN (:gyldigeStatuser)
            AND (b.saksbehandlingsansvarligIdent IS NULL OR :inkluderSaksbehandlingsansvarlige = TRUE)
             """
    )
    fun findBehandlingerBegrensetTilBehandlingsstatuser(
        @Param("status") status: Behandlingsstatus?,
        @Param("dokumentkategori") dokumentkategori: String?,
        @Param("behandlingstype") behandlingstype: Behandlingstype?,
        @Param("gyldigeStatuser") gyldigeStatuser: List<Behandlingsstatus>,
        @Param("inkluderSaksbehandlingsansvarlige") inkluderSaksbehandlingansvarlige: Boolean,
        pageable: Pageable
    ): Page<BehandlingEntity>

}