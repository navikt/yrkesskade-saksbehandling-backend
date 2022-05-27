package no.nav.yrkesskade.saksbehandling.model

import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "behandling_resultat")
class BehandlingResultatEntity(
    @Id
    @Column(name = "behandling_resultat_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val behandlingResultatId: Long,

    @Column(name = "resultat_tidspunkt")
    val resultattidspunkt: Instant,

    @Column(name = "sted")
    val sted: String,

    @Column(name = "bakgrunn_aarsak")
    val bakgrunnAarsak: String,

    @Column(name = "utfyllende_beskrivelse")
    val utfyllendeBeskrivelse: String,

    @ManyToOne
    @JoinColumn(name = "behandling_id")
    val behandling: BehandlingEntity
)