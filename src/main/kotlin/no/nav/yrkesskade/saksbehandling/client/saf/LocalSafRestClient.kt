package no.nav.yrkesskade.saksbehandling.client.saf

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.*

@Component
@Qualifier("safRestClient")
@Profile("local || integration")
class LocalSafRestClient : ISafRestClient {

    override fun hentDokument(journalpostId: String, dokumentinfoId: String): String {
        if (journalpostId == "-1") {
            throw SafException("Kunne ikke finne journalpost med id $journalpostId")
        }

        val bytes = LocalSafRestClient::class.java.getResourceAsStream("/sample/file-sample_150kb.pdf").readAllBytes()

        return Base64.getEncoder().encodeToString(bytes)
    }
}