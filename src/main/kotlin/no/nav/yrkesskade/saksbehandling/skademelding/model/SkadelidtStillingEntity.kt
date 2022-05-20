package no.nav.yrkesskade.saksbehandling.skademelding.model

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "skademelding_skadelidt_stilling")
class SkadelidtStillingEntity (
    @EmbeddedId val skadelidtStillingId: SkadelidtStillingId
)

@Embeddable
class SkadelidtStillingId (
    @Column(name ="skademelding_id")
    var skademeldingId: Long,
    @Column(name ="stillingstittel")
    var stillingstittel: String
) : Serializable
