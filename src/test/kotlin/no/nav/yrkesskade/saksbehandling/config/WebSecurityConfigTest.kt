package no.nav.yrkesskade.saksbehandling.config

import no.nav.security.mock.oauth2.http.post
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import no.nav.yrkesskade.saksbehandling.test.AbstractTest
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@EnableMockOAuth2Server
@AutoConfigureMockMvc
@SpringBootTest
class WebSecurityConfigTest : AbstractTest() {

    @Autowired
    lateinit var mvc: MockMvc

    @Test
    fun `uten token`() {
        mvc.perform(
            MockMvcRequestBuilders.post("/api/graphql")
                .content("""{"some": "data"}""")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL))
                .andExpect(status().isUnauthorized)
    }

    @Test
    fun `med token`() {
        val jwt = mvc.perform(MockMvcRequestBuilders.get("/oauth2/v2.0/token"))
            .andReturn().response.contentAsString

        mvc.perform(
            MockMvcRequestBuilders.post("/api/graphql")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $jwt")
                .content("""{"some": "data"}""")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL))
            .andExpect(status().isOk)
    }

    @Test
    fun `med ugyldig token`() {
    }
}

@ConditionalOnProperty(
    value = ["spring.profiles.active"],
    havingValue = "integration",
    matchIfMissing = false
)
@RestController
class DummyController {

    @PostMapping("api/graphql")
    fun graphqlDummyEndpoint(): String {
        return "Response"
    }
}