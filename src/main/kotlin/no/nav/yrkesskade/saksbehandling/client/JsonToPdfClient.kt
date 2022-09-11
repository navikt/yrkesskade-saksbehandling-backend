package no.nav.yrkesskade.saksbehandling.client

import no.nav.yrkesskade.saksbehandling.model.pdf.PdfInnholdElement
import no.nav.yrkesskade.saksbehandling.util.MDCConstants
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class JsonToPdfClient(
    private val jsonToPdfServiceWebClient: WebClient
) : AbstractRestClient("JsonToPdf") {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val log = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

    fun genererPdfFraJson(body: List<PdfInnholdElement>): ByteArray {
        return logTimingAndWebClientResponseException("jsonToPdf") {
            jsonToPdfServiceWebClient.post()
                .uri { it.path("/generer-pdf").build() }
                .header("Nav-Callid", MDC.get(MDCConstants.MDC_CALL_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono<ByteArray>()
                .block() ?: throw RuntimeException("Kunne ikke lage pdf av JSON")
        }
    }

}