package no.nav.yrkesskade.saksbehandling.api.v1.brev

import no.nav.yrkesskade.saksbehandling.client.JsonToPdfClient
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
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tannlegeerklaeringVeiledningbrev

@AutoConfigureMockMvc
class BrevControllerTest : AbstractTest() {

    companion object {
        private const val BREV_PATH = "/api/v1/brev"
    }

    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var jsonToPdfClient: JsonToPdfClient

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
    fun `generer brev`() {
        val tekstBytes = "Dette er en test".toByteArray(Charsets.UTF_8)
        Mockito.`when`(jsonToPdfClient.genererPdfFraJson(any())).thenReturn(tekstBytes)
        val jwt = token("azuread", "test@nav.test.no", "aad-client-id")
        val brev = tannlegeerklaeringVeiledningbrev()

        mvc.perform(
            MockMvcRequestBuilders.post("$BREV_PATH/generer")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $jwt")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(Charsets.UTF_8)
                .content(brev)
        ).andDo(MockMvcResultHandlers.print())
            .andExpect(status().is2xxSuccessful)
            .andExpect(content().string("RGV0dGUgZXIgZW4gdGVzdA=="))
    }
}