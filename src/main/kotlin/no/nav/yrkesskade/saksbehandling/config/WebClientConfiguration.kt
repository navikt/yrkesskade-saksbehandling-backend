package no.nav.yrkesskade.saksbehandling.config

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

@Configuration
class WebClientConfiguration(private val webClientBuilder: WebClient.Builder,
                             @Value("\${api.client.kodeverk.url}") val kodeverkServiceURL: String,
                             @Value("\${api.client.json-to-pdf.url}") val jsonToPdfServiceURL: String,
                             @Value("\${api.client.dokarkiv.url}") val dokarkivServiceURL: String
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

    @Bean
    fun dokarkivWebClient(): WebClient {
        return webClientBuilder
            .baseUrl(dokarkivServiceURL)
            .clientConnector(ReactorClientHttpConnector(HttpClient.newConnection()))
            .build()
    }

    @Bean
    fun kodeverkWebClient(): WebClient {
        return webClientBuilder
            .baseUrl(kodeverkServiceURL)
            .clientConnector(ReactorClientHttpConnector(HttpClient.newConnection()))
            .build()
    }

    @Bean
    fun jsonToPdfServiceWebClient(): WebClient {
        return webClientBuilder
            .baseUrl(jsonToPdfServiceURL)
            .clientConnector(ReactorClientHttpConnector(HttpClient.newConnection()))
            .build()
    }
}