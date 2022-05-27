package no.nav.yrkesskade.saksbehandling.model

import javax.persistence.*

@Entity
@Table(name = "dokument_fil")
class DokumentFilEntity (
    @Id
    @Column(name = "fil_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val filId: Long,

    @Column(name = "navn")
    val navn: String,

    @Column(name = "type")
    val type: String,

    @Column(name = "vedlegg_referanse")
    val vedleggreferanse: String,

    @Enumerated
    @Column(name = "status", nullable = true)
    val status: Filstatus?,

    @ManyToOne
    @JoinColumn(name = "dokument_id")
    val dokument: DokumentMetaEntity
)