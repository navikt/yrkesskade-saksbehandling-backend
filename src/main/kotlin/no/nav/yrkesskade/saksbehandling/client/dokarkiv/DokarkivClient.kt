package no.nav.yrkesskade.saksbehandling.client.dokarkiv

import no.nav.yrkesskade.saksbehandling.client.AbstractRestClient
import no.nav.yrkesskade.saksbehandling.util.TokenUtil
import no.nav.yrkesskade.saksbehandling.util.getLogger
import no.nav.yrkesskade.saksbehandling.util.getSecureLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.util.*

@Component
class DokarkivClient(
    private val dokarkivWebClient: WebClient,
    private val tokenUtil: TokenUtil,
    @Value("\${spring.application.name}") val applicationName: String
) : AbstractRestClient("Dokarkiv") {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val log = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    @Retryable
    fun ferdigstillJournalpost(ferdigstillJournalpostRequest: FerdigstillJournalpostRequest) {
        log.info("Ferdigstiller journalpost ${ferdigstillJournalpostRequest.journalpostId}")
        logTimingAndWebClientResponseException<Any>("ferdigstillJournalpost") {
            dokarkivWebClient.patch()
                .uri { uriBuilder ->
                    uriBuilder.pathSegment("rest")
                        .pathSegment("journalpostapi")
                        .pathSegment("v1")
                        .pathSegment("journalpost")
                        .pathSegment(ferdigstillJournalpostRequest.journalpostId)
                        .pathSegment("ferdigstill")
                        .build()
                }
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer ${tokenUtil.getAppAccessTokenWithDokarkivScope()}")
                .header("Nav-Callid", UUID.randomUUID().toString())
                .header("Nav-Consumer-Id", applicationName)
                .bodyValue(ferdigstillJournalpostRequest)
                .retrieve()
                .toBodilessEntity()
                .block() ?: throw RuntimeException("Kunne ikke ferdigstille journalpost")
        }
        return
    }

    private fun <T> logTimingAndWebClientResponseException(methodName: String, function: () -> ResponseEntity<Void>): ResponseEntity<Void> {
        val start: Long = System.currentTimeMillis()
        try {
            return function.invoke()
        } catch (ex: WebClientResponseException) {
            secureLogger.error(
                "Got a {} error calling Dokarkiv {} {} with message {}",
                ex.statusCode,
                ex.request?.method ?: "-",
                ex.request?.uri ?: "-",
                ex.responseBodyAsString
            )
            throw ex
        } catch (rtex: RuntimeException) {
            log.warn("Caught RuntimeException", rtex)
            throw rtex
        } finally {
            val end: Long = System.currentTimeMillis()
            log.info("Method {} took {} millis", methodName, (end - start))
        }
    }
}