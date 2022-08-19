package no.nav.yrkesskade.saksbehandling.fixtures

import no.nav.yrkesskade.saksbehandling.hendelser.oppgave.model.Oppgavestatuskategori
import no.nav.yrkesskade.saksbehandling.model.*
import java.time.Instant
import java.time.LocalDate

fun genererBehandling(behandlingId: Long, behandlingsansvarligIdent: String?, behandlingstatus: Behandlingsstatus, sak: SakEntity) : BehandlingEntity {
    return BehandlingEntity(
        aktivDato = LocalDate.now(),
        behandlingId = behandlingId,
        dokumentMetaer = emptyList(),
        opprettetAv = "test",
        endretAv = null,
        oppgaveId = "1",
        oppgavetype = "test",
        ansvarligEnhet = "test_enhet",
        behandlingResultater = emptyList(),
        behandlingsansvarligIdent = behandlingsansvarligIdent,
        opprettetTidspunkt = Instant.now(),
        statuskategori = Oppgavestatuskategori.AAPEN,
        status = behandlingstatus,
        fristFerdigstillelse = LocalDate.now().plusDays(10),
        behandlingstema = "test",
        sak = sak
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
        aktoerId = "012345678910",
        brukerIdentifikator = "012345678910",
        brukerFornavn = "Test",
        brukerMellomnavn = null,
        brukerEtternavn = "Testesen"
    )
}