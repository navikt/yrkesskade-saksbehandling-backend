package no.nav.yrkesskade.saksbehandling.model

import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "dokument")
class DokumentEntity (
    @Id
    @Column(name = "dokument_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val dokumentId: Long,

    @Column(name = "journalpost_id", nullable = false)
    val journalpostId: String,

    @Column(name = "dokument_navn", nullable = false)
    val dokumentnavn: String,

    @Enumerated
    @Column(name = "dokument_type", nullable = false)
    val dokumenttype: Dokumenttype,

    @Enumerated
    @Column(name = "dokument_kategori", nullable = false)
    val dokumentkategori: Dokumentkategori,

    @Column(name = "avsender_identifikator", nullable = false)
    val avsenderIdentifikator: String,

    @Column(name = "avsender_rolle", nullable = false)
    val avsenderrolle: String,

    @Column(name = "opprettet_tidspunkt", nullable = false)
    val opprettetTidspunkt: Instant,

    @ManyToOne
    @JoinColumn(name = "behandling_id")
    val behandling: BehandlingEntity,

    @OneToMany(mappedBy = "dokument")
    val filer: List<DokumentfilEntity>
)