package no.nav.yrkesskade.saksbehandling.skademelding.model

import no.nav.yrkesskade.skademelding.model.Tidstype
import java.time.Instant
import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "skademelding")
class SkademeldingEntity(
    @Id
    @Column(name = "skademelding_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val skademeldingId: Long,

    @Column(name = "innmelder_identitetsnummer", nullable = false)
    val innmelderIdentitetsnummer: String,

    @Column(name = "innmelder_paa_vegne_av", nullable = false)
    val innmelderPaaVegneAv: String,

    @Column(name = "innmelder_virksomhetsnavn", nullable = false)
    val innmelderVirksomhetsnavn: String,

    @Column(name = "innmelder_rolle", nullable = false)
    val innmelderRolle: String,

    @Column(name = "skadelidt_identitetsnummer", nullable = false)
    val skadelidtIdentitetsnummer: String,

    @Column(name = "skadelidt_rolletype", nullable = false)
    val skadelidtRolletype: String,

    @Column(name = "dekning_organisasjonsnummer", nullable = false)
    val dekningsOrganisasjonsnummer: String,

    @Column(name = "dekning_navn_paa_virksomheten", nullable = false)
    val dekningVirksomhetsnavn: String,

    @Column(name = "skade_alvorlighetsgrad", nullable = true)
    val skadeAlvorlighetsgrad: String?,

    @Column(name = "hendelse_naar_skjedde_ulykken", nullable = false)
    val hendelseNaarSkjeddeUlykken: String,

    @Column(name = "hendelse_hvor_skjedde_ulykken", nullable = false)
    val hendelseHvorSkjeddeUlykken: String,

    @Column(name = "hendelse_aarsak_ulykke", nullable = false)
    val hendelseAarsakUlykke: String,

    @Column(name = "hendelse_bakgrunn_ulykke", nullable = false)
    val hendelseBakgrunnUlykke: String,

    @Column(name = "hendelse_utfyllende_beskrivelse", nullable = false)
    val hendelseUtfyllendeBeskrivelse: String,

    @Enumerated
    @Column(name = "hendelse_tidstype", nullable = false)
    val hendelseTidstype: Tidstype,

    @Column(name = "hendelse_tidspunkt", nullable = true)
    val hendelseTidspunkt: Instant?,

    @Column(name = "hendelse_periode_fra", nullable = true)
    val hendelsePeriodeFra: LocalDate?,

    @Column(name = "hendelse_periode_til", nullable = true)
    val hendelsePeriodeTil: LocalDate?,

    @Column(name = "hendelse_steds_beskrivelse", nullable = false)
    val hendelseStedsbeskrivelse: String,

    @OneToMany
    @JoinColumn(name = "skademelding_id")
    var skadedeDeler: Set<SkadetDelEntity>?,

    @OneToMany
    @JoinColumn(name = "skademelding_id")
    var stillingstitler: Set<SkadelidtStillingEntity>?,

    @OneToOne(mappedBy = "skademelding", cascade = [CascadeType.ALL])
    @PrimaryKeyJoinColumn
    var virksomhetsAdressse: VirksomhetsAdresseEntity?,

    @OneToOne(mappedBy = "skademelding", cascade = [CascadeType.ALL])
    @PrimaryKeyJoinColumn
    var bostedsAdressse: BostedsAdressseEntity?,

    @OneToOne(mappedBy = "skademelding", cascade = [CascadeType.ALL])
    @PrimaryKeyJoinColumn
    var ulykkessted: UlykkesstedEntity?
)
