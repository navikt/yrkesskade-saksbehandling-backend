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

    @Column(name = "tema", nullable = false)
    val tema: String,

    @Enumerated
    @Column(name = "sakstype", nullable = false)
    val sakstype: Sakstype,

    @Column(name = "bruker_identifikator", nullable = false)
    val brukerIdentifikator: String,

    @Column(name = "opprettet_tidspunkt", nullable = false)
    val opprettetTidspunkt: Instant,

    @Column(name = "opprettet_av", nullable = false)
    val opprettetAv: String,

    @OneToMany(mappedBy = "sak")
    val behandlinger: List<BehandlingEntity>,

    @Enumerated
    @Column(name = "saksstatus", nullable = false)
    val saksstatus: Saksstatus
)
