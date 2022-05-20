package no.nav.yrkesskade.saksbehandling.skademelding.model

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "skadet_del")
class SkadetDelEntity(
    @EmbeddedId val skadetDelId: SkadetDelId
)

@Embeddable
data class SkadetDelId(
    @Column(name = "skademelding_id")
    var skademeldingId: Long,
    @Column(name = "skadeart")
    var skadeart: String,
    @Column(name = "kroppsdel")
    var kroppsdel: String
) : Serializable
