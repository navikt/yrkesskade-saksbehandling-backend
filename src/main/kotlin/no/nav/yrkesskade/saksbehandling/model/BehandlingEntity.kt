package no.nav.yrkesskade.saksbehandling.model

import com.expediagroup.graphql.generated.enums.BrukerIdType
import no.nav.yrkesskade.saksbehandling.model.dto.BehandlingDto
import no.nav.yrkesskade.saksbehandling.util.kodeverk.KodeverdiMapper
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "behandling")
data class BehandlingEntity (

    @Id
    @Column(name = "behandling_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val behandlingId: Long,

    @Column(name = "tema", nullable = false)
    val tema: String,

    @Column(name = "bruker_id", nullable = false)
    val brukerId: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "bruker_id_type", nullable = false)
    val brukerIdType: BrukerIdType,

    @Column(name = "behandlende_enhet", nullable = false)
    val behandlendeEnhet: String?,

    @Column(name = "saksbehandlingsansvarlig_ident", nullable = true)
    val saksbehandlingsansvarligIdent: String? = null,

    @Enumerated
    @Column(name = "behandlingstype", nullable = false)
    val behandlingstype: Behandlingstype,

    @Enumerated
    @Column(name = "status", nullable = false)
    val status: Behandlingsstatus,

    @Column(name = "behandlingsfrist", nullable = false)
    val behandlingsfrist: Instant,

    @Column(name = "journalpost_id", nullable = false)
    val journalpostId: String,

    @Column(name = "dokumentkategori", nullable = false)
    val dokumentkategori: String,

    @Column(name = "systemreferanse", nullable = false)
    val systemreferanse: String,

    @Enumerated
    @Column(name = "framdriftsstatus", nullable = false)
    val framdriftsstatus: Framdriftsstatus,

    @Column(name = "opprettet_tidspunkt", nullable = false)
    val opprettetTidspunkt: Instant,

    @Column(name = "opprettet_av", nullable = false)
    val opprettetAv: String,

    @Column(name = "endret_av", nullable = true)
    val endretAv: String? = null,

    @ManyToOne
    @JoinColumn(name = "sak_id", nullable = true)
    val sak: SakEntity? = null,

    @OneToMany(mappedBy = "behandling")
    val behandlingResultater: List<BehandlingsresultatEntity>
) {

    fun toBehandlingDto(kodeverdiMapper: KodeverdiMapper): BehandlingDto {
        return BehandlingDto(
            behandlingId,
            tema,
            brukerId,
            brukerIdType,
            behandlendeEnhet,
            saksbehandlingsansvarligIdent,
            kodeverdiMapper.mapBehandlingstype(behandlingstype),
            kodeverdiMapper.mapBehandlingsstatus(status),
            behandlingsfrist,
            journalpostId,
            dokumentkategori,
            systemreferanse,
            kodeverdiMapper.mapFramdriftsstatus(framdriftsstatus),
            opprettetTidspunkt,
            opprettetAv,
            endretAv,
            sak,
            behandlingResultater
        )
    }

}