package no.nav.yrkesskade.saksbehandling.model

import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "behandlingsoverforing_log")
data class BehandlingsoverfoeringLogEntity (

    @Id
    @Column(name = "overfoering_log_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val overfoeringLogId: Long,

    @Column(name = "overfoert_tidspunkt", nullable = false)
    val overfoertTidspunkt: Instant,

    @Column(name = "overfoert_av", nullable = false)
    val overfoertAv: String,

    @Column(name = "journalpost_id", nullable = false)
    val journalpostId: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "behandlingstype", nullable = false)
    val behandlingstype: Behandlingstype,

    @Column(name = "avviksbegrunnelse", nullable = false)
    val avviksbegrunnelse: String,

    @Column(name = "overfoert_til_system", nullable = false)
    val overfoertTilSystem: String,
)