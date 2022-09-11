package no.nav.yrkesskade.saksbehandling.client.kodeverk

import no.nav.yrkesskade.kodeverk.model.KodeverdiDto
import no.nav.yrkesskade.kodeverk.model.KodeverdiResponsDto
import no.nav.yrkesskade.saksbehandling.client.AbstractRestClient
import no.nav.yrkesskade.saksbehandling.util.MDCConstants
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class Kodeverkklient(
    private val kodeverkWebClient: WebClient
) : AbstractRestClient("Kodeverk") {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val log = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

    @Retryable
    fun hentKodeverk(type: String, kategori: String?, spraak: String = "nb"): Map<String, KodeverdiDto> {
        log.info("Kaller ys-kodeverk - type=$type, kategori=$kategori, spraak=$spraak")
        return logTimingAndWebClientResponseException("hentLand") {
            kallKodeverkApi(type, kategori)
        }
    }

    private fun kallKodeverkApi(type: String, kategori: String?): Map<String, KodeverdiDto> {
        val uriPath = if (kategori.isNullOrBlank()) "api/v1/kodeverk/typer/$type/kodeverdier" else "api/v1/kodeverk/typer/$type/kategorier/$kategori/kodeverdier"
        val kodeverdiRespons = kodeverkWebClient.get()
            .uri { uriBuilder -> uriBuilder.path(uriPath).build() }
            .header("Nav-Callid", MDC.get(MDCConstants.MDC_CALL_ID))
            .retrieve()
            .bodyToMono<KodeverdiResponsDto>()
            .block() ?: KodeverdiResponsDto(emptyMap())

        return kodeverdiRespons.kodeverdierMap.orEmpty()
    }
}