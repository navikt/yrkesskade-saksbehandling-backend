package no.nav.yrkesskade.saksbehandling.fixtures

import no.nav.yrkesskade.saksbehandling.client.dokarkiv.FerdigstillJournalpostRequest

fun ferdigstillJournalpostRequest() = FerdigstillJournalpostRequest(
    journalpostId = "123456",
    journalfoerendeEnhet = "1111"
)
