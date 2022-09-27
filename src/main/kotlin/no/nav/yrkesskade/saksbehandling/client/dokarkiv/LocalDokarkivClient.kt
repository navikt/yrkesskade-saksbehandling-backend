package no.nav.yrkesskade.saksbehandling.client.dokarkiv

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Qualifier("dokarkivClient")
@Profile("local || integration")
class LocalDokarkivClient : IDokarkivClient {
    override fun ferdigstillJournalpost(
        journalpostId: String,
        ferdigstillJournalpostRequest: FerdigstillJournalpostRequest
    ) {
        return
    }
}