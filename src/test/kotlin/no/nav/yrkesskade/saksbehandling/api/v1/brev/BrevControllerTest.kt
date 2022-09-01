package no.nav.yrkesskade.saksbehandling.api.v1.brev

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import no.nav.yrkesskade.saksbehandling.api.v1.brev.dto.BrevDto
import no.nav.yrkesskade.saksbehandling.test.AbstractTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tannlegeerklaeringVeiledningbrev

@AutoConfigureMockMvc
class BrevControllerTest : AbstractTest() {

    companion object {
        private const val BREV_PATH = "/api/v1/brev"
    }

    @Autowired
    lateinit var server: MockOAuth2Server

    @Autowired
    lateinit var mvc: MockMvc

    @Test
    fun `send tannlegeerklæring veilednings brev`() {
        val jwt = token("azuread", "test@nav.test.no", "aad-client-id")
        val brev = tannlegeerklaeringVeiledningbrev()
        val brevDto = jacksonObjectMapper().readValue(brev, BrevDto::class.java)
        assertThat(brevDto).isNotNull

        postBrev(brev, jwt).andDo(MockMvcResultHandlers.print()).andExpect(status().isAccepted)
    }

    private fun postBrev(brev: String, token: String) =
        mvc.perform(
            MockMvcRequestBuilders.post(BREV_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(Charsets.UTF_8)
                .content(brev)
        )

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