package no.nav.yrkesskade.saksbehandling.config

import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import no.nav.security.token.support.core.api.Unprotected
import no.nav.yrkesskade.saksbehandling.test.AbstractTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@AutoConfigureMockMvc
class WebSecurityConfigTest : AbstractTest() {

    @Autowired
    lateinit var server: MockOAuth2Server

    @Autowired
    lateinit var mvc: MockMvc

    @Test
    fun `uten token`() {
        mvc.perform(
            MockMvcRequestBuilders.get("/api/graphql"))
                .andExpect(status().isUnauthorized)
    }

    @Test
    fun `med token`() {
        val jwt = token("azuread", "test@nav.test.no", "aad-client-id");

        mvc.perform(
            MockMvcRequestBuilders.get("/api/graphql")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $jwt"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
    }

    @Test
    fun `med ugyldig token`() {
        val jwt = token("ugyldig", "test@nav.test.no", "ugyldig");

        mvc.perform(
            MockMvcRequestBuilders.get("/api/graphql")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $jwt"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isUnauthorized)
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

@RestController
@Unprotected
@RequestMapping("/api/graphql")
class DummyController {

    @GetMapping
    fun graphqlDummyEndpoint(): String {
        return "OKIDOKI"
    }
}