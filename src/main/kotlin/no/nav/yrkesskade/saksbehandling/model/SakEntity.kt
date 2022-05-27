package no.nav.yrkesskade.saksbehandling.model

import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "sak")
data class SakEntity(
    @Id
    @Column(name = "sak_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val sakId: Long,

    @Enumerated
    @Column(name = "sak_status", nullable = false)
    val sakstatus: Sakstatus,

    @Column(name = "opprettet_tidspunkt", nullable = false)
    val opprettetTidspunkt: Instant,

    @Column(name = "opprettet_av", nullable = false)
    val opprettetAv: String,

    @Enumerated
    @Column(name = "sakstype", nullable = false)
    val sakstype: Sakstype,

    @Column(name = "aktoerId", nullable = false)
    val aktoerId: String,

    @Column(name = "bruker_identifikator", nullable = false)
    val brukerIdentifikator: String,

    @Column(name = "bruker_fornavn", nullable = false)
    val brukerFornavn: String,

    @Column(name = "bruker_mellomnavn", nullable = true)
    val brukerMellomnavn: String?,

    @Column(name = "bruker_etternavn", nullable = false)
    val brukerEtternavn: String,

    @OneToMany(mappedBy = "sak")
    val behandlinger: List<BehandlingEntity>
)
