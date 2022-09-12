package no.nav.yrkesskade.saksbehandling.model

import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "behandlingsresultat")
class BehandlingsresultatEntity(
    @Id
    @Column(name = "behandlingsresultat_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val behandlingsresultatId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "resultat")
    val resultat: Behandlingsresultat,

    @Column(name = "resultat_tidspunkt")
    val resultattidspunkt: Instant,

    @Column(name = "sted")
    val sted: String,

    @Column(name = "bakgrunnsaarsak")
    val bakgrunnsaarsak: String,

    @Column(name = "utfyllende_beskrivelse")
    val utfyllendeBeskrivelse: String,

    @ManyToOne
    @JoinColumn(name = "behandling_id")
    val behandling: BehandlingEntity
)