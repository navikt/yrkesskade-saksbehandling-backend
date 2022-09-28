package no.nav.yrkesskade.saksbehandling.client.dokarkiv

import no.nav.yrkesskade.saksbehandling.client.AbstractRestClient
import no.nav.yrkesskade.saksbehandling.util.MDCConstants
import no.nav.yrkesskade.saksbehandling.util.TokenUtil
import no.nav.yrkesskade.saksbehandling.util.getLogger
import no.nav.yrkesskade.saksbehandling.util.getSecureLogger
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.http.MediaType
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@Component
@Profile("!local")
class DokarkivClient(
    private val dokarkivWebClient: WebClient,
    private val tokenUtil: TokenUtil,
    @Value("\${spring.application.name}") val applicationName: String
) : AbstractRestClient("Dokarkiv"), IDokarkivClient {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val log = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    @Retryable
    override fun ferdigstillJournalpost(journalpostId: String, ferdigstillJournalpostRequest: FerdigstillJournalpostRequest) {
        log.info("Ferdigstiller journalpost $journalpostId")
        logTimingAndWebClientResponseException<Any>("ferdigstillJournalpost") {
            dokarkivWebClient.patch()
                .uri { uriBuilder ->
                    uriBuilder.pathSegment("rest")
                        .pathSegment("journalpostapi")
                        .pathSegment("v1")
                        .pathSegment("journalpost")
                        .pathSegment(journalpostId)
                        .pathSegment("ferdigstill")
                        .build()
                }
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer ${tokenUtil.getAppAccessTokenWithDokarkivScope()}")
                .header("Nav-Callid", MDC.get(MDCConstants.MDC_CALL_ID))
                .header("Nav-Consumer-Id", applicationName)
                .bodyValue(ferdigstillJournalpostRequest)
                .retrieve()
                .toBodilessEntity()
                .block() ?: throw RuntimeException("Kunne ikke ferdigstille journalpost")
        }
        return
    }

}