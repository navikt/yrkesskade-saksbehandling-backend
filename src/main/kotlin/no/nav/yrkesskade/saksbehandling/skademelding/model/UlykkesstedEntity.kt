package no.nav.yrkesskade.saksbehandling.skademelding.model

import javax.persistence.*

@Entity
@Table(name = "skademelding_ulykkessted")
class UlykkesstedEntity(
    @Id
    @Column(name = "skademelding_id")
    val skademeldingId: Long,
    @Column(name = "samme_som_virksomhetens_adresse")
    val sammeSomVirksomhetsAdressse: Boolean,
    @Column(name = "adresselinje_1")
    val adresselinje1: String,
    @Column(name = "adresselinje_2")
    val adresselinje2: String,
    @Column(name = "adresselinje_3")
    val adresselinje3: String,
    @Column(name = "landkode")
    val landkode: String,

    @OneToOne
    @MapsId
    @JoinColumn(name = "skademelding_id")
    val skademelding: SkademeldingEntity?
)
