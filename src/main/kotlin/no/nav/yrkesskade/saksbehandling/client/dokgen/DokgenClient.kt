package no.nav.yrkesskade.saksbehandling.client.dokgen

import no.nav.yrkesskade.saksbehandling.client.AbstractRestClient
import no.nav.yrkesskade.saksbehandling.model.pdf.PdfData
import no.nav.yrkesskade.saksbehandling.model.pdf.PdfTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.netty.http.client.HttpClient

@Component
class DokgenClient(
    private val webClientBuilder: WebClient.Builder,
    @Value("\${YRKESSKADE_DOKGEN_API_URL}") val pdfServiceURL: String,
) : AbstractRestClient("PDF Dokgen") {

    val pdfWebClient: WebClient

    init {
        pdfWebClient = webClientBuilder
            .baseUrl(pdfServiceURL)
            .clientConnector(ReactorClientHttpConnector(HttpClient.newConnection()))
            .build()
    }

    @Retryable
    fun lagPdf(pdfData: PdfData, template: PdfTemplate): ByteArray {
        log.info("Lager pdf av typen ${template.templatenavn}")
        val prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pdfData)
        secureLogger.info("Lager pdf med data:\n\r$prettyJson")
        return logTimingAndWebClientResponseException("lagPdf") {
            pdfWebClient.post()
                .uri { uriBuilder ->
                    uriBuilder.pathSegment("template")
                        .pathSegment(template.templatenavn)
                        .pathSegment("download-pdf")
                        .build()
                }
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(pdfData)
                .retrieve()
                .bodyToMono<ByteArray>()
                .block() ?: throw RuntimeException("Kunne ikke lage pdf")
        }.also {
            log.info("Opprettet pdf $template")
        }
    }
}