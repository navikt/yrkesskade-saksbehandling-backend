package no.nav.yrkesskade.saksbehandling.fixtures

import com.expediagroup.graphql.generated.enums.BrukerIdType
import com.expediagroup.graphql.generated.enums.Tema
import no.nav.yrkesskade.saksbehandling.model.*
import java.time.Instant
import java.time.temporal.ChronoUnit

fun genererBehandling(
    behandlingId: Long,
    behandlingsansvarligIdent: String?,
    behandlingstatus: Behandlingsstatus,
    sak: SakEntity,
    behandlingstype: Behandlingstype = Behandlingstype.VEILEDNING
) : BehandlingEntity {
    return BehandlingEntity(
        behandlingId = behandlingId,
        opprettetAv = "test",
        endretAv = null,
        behandlingResultater = emptyList(),
        opprettetTidspunkt = Instant.now(),
        status = behandlingstatus,
        tema = Tema.YRK.name,
        brukerId = "12345",
        brukerIdType = BrukerIdType.AKTOERID,
        behandlendeEnhet = "9999",
        sak = sak,
        saksbehandlingsansvarligIdent = behandlingsansvarligIdent,
        behandlingstype = behandlingstype,
        behandlingsfrist = Instant.now().plus(30, ChronoUnit.DAYS),
        journalpostId = "213123123",
        dokumentkategori = "enFinKategori",
        systemreferanse = "referanse",
        framdriftsstatus = Framdriftsstatus.IKKE_PAABEGYNT
    )
}

fun genererSak() : SakEntity {
    return SakEntity(
        sakId = 1L,
        saksstatus = Saksstatus.AAPEN,
        opprettetTidspunkt = Instant.now(),
        opprettetAv = "test",
        sakstype = Sakstype.YRKESSYKDOM,
        behandlinger = emptyList(),
        brukerIdentifikator = "012345678910",
        tema = Tema.YRK.name
    )
}