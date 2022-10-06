package no.nav.yrkesskade.saksbehandling.api.v1.dokument

import no.nav.yrkesskade.saksbehandling.client.saf.SafRestClient
import no.nav.yrkesskade.saksbehandling.fixtures.behandlingsstatus
import no.nav.yrkesskade.saksbehandling.fixtures.behandlingstyper
import no.nav.yrkesskade.saksbehandling.fixtures.dokumentkategori
import no.nav.yrkesskade.saksbehandling.fixtures.framdriftsstatus
import no.nav.yrkesskade.saksbehandling.service.KodeverkService
import no.nav.yrkesskade.saksbehandling.test.AbstractTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.*

@AutoConfigureMockMvc
class DokumentInfoControllerTest : AbstractTest() {

    companion object {
        private const val DOKUMENT_INFO_PATH = "/api/v1/dokumentinfo/{journalpostid}/{dokumentinfoid}"
    }

    @MockBean
    lateinit var safRestClient: SafRestClient

    @MockBean
    lateinit var kodeverkService: KodeverkService

    @Autowired
    lateinit var mvc: MockMvc

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
    fun `hent dokumentinfo - med gyldig token`() {
        Mockito.`when`(safRestClient.hentDokument(anyString(), anyString())).thenReturn(Base64.getEncoder().encode("test".toByteArray()).toString())
        val jwt = token("azuread", "test@nav.test.no", "aad-client-id")

        mvc.perform(
            MockMvcRequestBuilders.get(DOKUMENT_INFO_PATH, "1", "1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $jwt")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(Charsets.UTF_8)
        ).andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `hent dokumentinfo - uten gyldig token`() {
        mvc.perform(
            MockMvcRequestBuilders.get(DOKUMENT_INFO_PATH, "1", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(Charsets.UTF_8)
        ).andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().is4xxClientError)
    }
}