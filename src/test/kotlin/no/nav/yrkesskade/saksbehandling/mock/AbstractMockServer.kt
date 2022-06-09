package no.nav.yrkesskade.saksbehandling.mock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer
import com.github.tomakehurst.wiremock.matching.UrlPattern
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles
import java.time.Instant

fun WireMockServer.stubForAny(urlPattern: UrlPattern, builder: MappingBuilder.() -> Unit) {
    stubFor(any(urlPattern).apply(builder))
}

fun MappingBuilder.willReturnJson(body: String) {
    willReturn(
        aResponse().apply {
            withHeader("Content-Type", "application/json")
            withBody(body)
        }
    )
}

open class AbstractMockServer(private val port: Int?) {

    private val log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())

    private val TOKEN_RESPONSE_TEMPLATE = """{
            "token_type": "Bearer",
            "scope": "%s",
            "expires_at": %s,
            "ext_expires_in": %s,
            "expires_in": %s,
            "access_token": "%s"
        }"""

    private val mockServer: WireMockServer

    init {
        val config = WireMockConfiguration().apply {
            if (port != null) {
                port(port)
            } else {
                dynamicPort()
            }
            extensions(ResponseTemplateTransformer(true))
        }

        mockServer = WireMockServer(config).apply {
            setup()
        }
    }

    fun start() {
        if (!mockServer.isRunning) {
            mockServer.start()
        }
    }

    fun stop() {
        if (mockServer.isRunning) {
            mockServer.stop()
        }
    }

    fun port(): Int {
        return mockServer.port()
    }

    private fun WireMockServer.setup() {

        // Oauth2 token
        log.info("Wiremock stub /oauth2/v2.0/token")
        stubForAny(urlPathMatching("/oauth2/v2.0/token")) {
            val response = String.format(
                TOKEN_RESPONSE_TEMPLATE,
                "test_scope",
                Instant.now().plusSeconds(3600).getEpochSecond(),
                30,
                30,
                "somerandomtoken"
            )
            willReturnJson(response)
        }
    }
}