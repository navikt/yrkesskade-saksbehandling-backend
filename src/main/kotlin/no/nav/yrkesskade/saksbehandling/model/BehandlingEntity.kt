package no.nav.yrkesskade.saksbehandling.model

import no.nav.yrkesskade.saksbehandling.hendelser.oppgave.model.Oppgavestatuskategori
import java.time.Instant
import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "behandling")
class BehandlingEntity (

    @Id
    @Column(name = "behandling_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val behandlingId: Long,

    @Column(name = "behandlingsansvarlig_ident", nullable = true)
    var behandlingsansvarligIdent: String?,

    @Enumerated
    @Column(name = "status", nullable = false)
    var status: Behandlingsstatus,

    @Enumerated
    @Column(name = "statuskategori", nullable = false)
    val statuskategori: Oppgavestatuskategori,

    @Column(name = "ansvarlig_enhet", nullable = false)
    val ansvarligEnhet: String?,

    @Column(name = "behandlingstema", nullable = false)
    val behandlingstema: String,

    @Column(name = "oppgave_id", nullable = false)
    val oppgaveId: String,

    @Column(name = "oppgave_type", nullable = false)
    val oppgavetype: String,

    @Column(name = "frist_ferdigstillelse", nullable = false)
    val fristFerdigstillelse: LocalDate,

    @Column(name = "aktiv_dato", nullable = false)
    val aktivDato: LocalDate,

    @Column(name = "opprettet_tidspunkt", nullable = false)
    val opprettetTidspunkt: Instant,

    @Column(name = "opprettet_av", nullable = false)
    val opprettetAv: String,

    @Column(name = "endret_av", nullable = true)
    val endretAv: String?,

    @ManyToOne
    @JoinColumn(name = "sak_id")
    val sak: SakEntity,

    @OneToMany(mappedBy = "behandling")
    val dokumentMetaer: List<DokumentEntity>,

    @OneToMany(mappedBy = "behandling")
    val behandlingResultater: List<BehandlingsresultatEntity>
)