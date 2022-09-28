package no.nav.yrkesskade.saksbehandling.client.dokarkiv

interface IDokarkivClient {
    fun ferdigstillJournalpost(journalpostId: String, ferdigstillJournalpostRequest: FerdigstillJournalpostRequest)
}