package no.nav.yrkesskade.saksbehandling.api

import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import no.nav.yrkesskade.saksbehandling.test.AbstractTest
import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath


@AutoConfigureMockMvc
class BehandlingstypeControllerTest : AbstractTest() {

    @Autowired
    lateinit var server: MockOAuth2Server

    @Autowired
    lateinit var mvc: MockMvc

    @Test
    fun `hent behandlingstyper - autentisert`() {
        val jwt = token("azuread", "test@nav.test.no", "aad-client-id");

        mvc.perform(get(BEHANDLINGSTYPER_V1_PATH).header(HttpHeaders.AUTHORIZATION, "Bearer $jwt"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath("$.length()").value(6))
    }

    companion object {
        private const val BEHANDLINGSTYPER_V1_PATH = "/v1/behandlingstyper"
    }

    private fun token(issuerId: String, subject: String, audience: String): String {
        return server.issueToken(
            issuerId = issuerId,
            clientId = "theclientid",
            tokenCallback = DefaultOAuth2TokenCallback(
                issuerId = issuerId,
                subject = subject,
                audience = listOf(audience),
                claims = emptyMap(),
                expiry = 3600L
            )
        ).serialize()
    }
}