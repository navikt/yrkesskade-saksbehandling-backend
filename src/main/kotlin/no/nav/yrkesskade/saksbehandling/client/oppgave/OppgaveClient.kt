package no.nav.yrkesskade.saksbehandling.client.oppgave

import no.nav.yrkesskade.saksbehandling.client.AbstractRestClient
import no.nav.yrkesskade.saksbehandling.util.MDCConstants
import no.nav.yrkesskade.saksbehandling.util.TokenUtil
import no.nav.yrkesskade.saksbehandling.util.getSecureLogger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class OppgaveClient(
    private val oppgaveWebClient: WebClient,
    private val tokenUtil: TokenUtil,
    @Value("\${spring.application.name}") val applicationName: String
) : AbstractRestClient("Oppgave") {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val log = LoggerFactory.getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    @Retryable
    fun opprettOppgave(oppgave: OpprettJournalfoeringOppgave): Oppgave {
        log.info("Oppretter oppgave for journalpostId ${oppgave.journalpostId}")
        return logTimingAndWebClientResponseException("opprettOppgave") {
            oppgaveWebClient.post()
                .uri { uriBuilder ->
                    uriBuilder.pathSegment("api")
                        .pathSegment("v1")
                        .pathSegment("oppgaver")
                        .build()
                }
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer ${tokenUtil.getAppAccessTokenWithOppgaveScope()}")
                .header("X-Correlation-ID", MDC.get(MDCConstants.MDC_CALL_ID))
                .header("Nav-Consumer-Id", applicationName)
                .bodyValue(oppgave)
                .retrieve()
                .bodyToMono<Oppgave>()
                .block() ?: throw RuntimeException("Kunne ikke lage oppgave")
        }.also {
            log.info("Opprettet journalføringsoppgave for journalpostId ${oppgave.journalpostId}")
        }
    }
}