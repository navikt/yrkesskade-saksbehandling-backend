package no.nav.yrkesskade.saksbehandling.config

import no.nav.security.token.support.core.api.Unprotected
import no.nav.yrkesskade.saksbehandling.fixtures.behandlingsstatus
import no.nav.yrkesskade.saksbehandling.fixtures.behandlingstyper
import no.nav.yrkesskade.saksbehandling.fixtures.dokumentkategori
import no.nav.yrkesskade.saksbehandling.fixtures.framdriftsstatus
import no.nav.yrkesskade.saksbehandling.service.KodeverkService
import no.nav.yrkesskade.saksbehandling.test.AbstractTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.mock.mockito.MockBean
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
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var kodeverkService: KodeverkService

    @BeforeEach
    fun setUp() {
        mockKodeverk()
    }

    fun mockKodeverk() {
        Mockito.`when`(kodeverkService.hentKodeverk(eq("behandlingstype"), eq(null), any())).thenReturn(behandlingstyper())
        Mockito.`when`(kodeverkService.hentKodeverk(eq("behandlingsstatus"), eq(null), any())).thenReturn(
            behandlingsstatus()
        )
        Mockito.`when`(kodeverkService.hentKodeverk(eq("framdriftsstatus"), eq(null), any())).thenReturn(
            framdriftsstatus()
        )
        Mockito.`when`(kodeverkService.hentKodeverk(eq("dokumenttype"), eq(null), any())).thenReturn(dokumentkategori())
    }

    @Test
    fun `uten token`() {
        mvc.perform(
            MockMvcRequestBuilders.get("/api/graphql"))
                .andExpect(status().isUnauthorized)
    }

    @Test
    fun `med token`() {
        val jwt = token("azuread", "test@nav.test.no", "aad-client-id")

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