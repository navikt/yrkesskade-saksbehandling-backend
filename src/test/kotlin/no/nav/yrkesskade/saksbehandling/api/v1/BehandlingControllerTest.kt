package no.nav.yrkesskade.saksbehandling.api.v1

import com.expediagroup.graphql.generated.enums.IdentGruppe
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.yrkesskade.saksbehandling.fixtures.hentIdenterResultMedFnrUtenHistorikk
import no.nav.yrkesskade.saksbehandling.client.JsonToPdfClient
import no.nav.yrkesskade.saksbehandling.fixtures.behandlingsstatus
import no.nav.yrkesskade.saksbehandling.fixtures.behandlingstyper
import no.nav.yrkesskade.saksbehandling.fixtures.dokumentkategori
import no.nav.yrkesskade.saksbehandling.fixtures.framdriftsstatus
import no.nav.yrkesskade.saksbehandling.fixtures.genererBehandling
import no.nav.yrkesskade.saksbehandling.fixtures.genererSak
import no.nav.yrkesskade.saksbehandling.graphql.client.pdl.PdlClient
import no.nav.yrkesskade.saksbehandling.model.Behandlingsstatus
import no.nav.yrkesskade.saksbehandling.model.Brev
import no.nav.yrkesskade.saksbehandling.repository.BehandlingRepository
import no.nav.yrkesskade.saksbehandling.repository.SakRepository
import no.nav.yrkesskade.saksbehandling.service.KodeverkService
import no.nav.yrkesskade.saksbehandling.test.AbstractTest
import no.nav.yrkesskade.saksbehandling.util.Tokentype
import org.assertj.core.api.Assertions
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional
import tannlegeerklaeringVeiledningbrev

@AutoConfigureMockMvc
internal class BehandlingControllerTest : AbstractTest() {

    companion object {
        private const val PATH = "/api/v1/behandlinger"
    }

    @Autowired
    lateinit var mvc: MockMvc

    @Autowired
    lateinit var behandlingRepository: BehandlingRepository

    @Autowired
    lateinit var sakRepository: SakRepository

    @MockBean
    lateinit var kodeverkService: KodeverkService

    @MockBean
    lateinit var jsonToPdfClient: JsonToPdfClient

    @MockBean
    lateinit var pdlClient: PdlClient

    var behandlingId: Long = 0

    var jwt: String = ""

    @BeforeEach
    fun setUp() {
        resetDatabase()
        val sak = sakRepository.save(genererSak())

        val behandling = genererBehandling(1L, "test", Behandlingsstatus.IKKE_PAABEGYNT, sak)
        val behandlingEntity = behandlingRepository.save(behandling)
        behandlingId = behandlingEntity.behandlingId

        Mockito.`when`(kodeverkService.hentKodeverk(eq("behandlingstype"), eq(null), any())).thenReturn(behandlingstyper())
        Mockito.`when`(kodeverkService.hentKodeverk(eq("behandlingsstatus"), eq(null), any())).thenReturn(behandlingsstatus())
        Mockito.`when`(kodeverkService.hentKodeverk(eq("framdriftsstatus"), eq(null), any())).thenReturn(framdriftsstatus())
        Mockito.`when`(kodeverkService.hentKodeverk(eq("dokumenttype"), eq(null), any())).thenReturn(dokumentkategori())

        Mockito.`when`(pdlClient.hentIdenter(
            eq("12345"),
            eq(listOf(IdentGruppe.FOLKEREGISTERIDENT)),
            eq(false),
            eq(Tokentype.ON_BEHALF_OF)
        )).thenReturn(
            hentIdenterResultMedFnrUtenHistorikk()
        )
        val tekstBytes = "Dette er en test".toByteArray(Charsets.UTF_8)
        Mockito.`when`(jsonToPdfClient.genererPdfFraJson(any())).thenReturn(tekstBytes)

        jwt = token("azuread", "test@nav.test.no", "aad-client-id")
    }

    @Transactional
    fun resetDatabase() {
        behandlingRepository.deleteAll()
        sakRepository.deleteAll()
    }

    @Test
    fun `send tannlegeerklæring veiledningsbrev`() {
        val brevSomStreng = hentTannlegeerklaeringVeiledningbrev()

        mvc.perform(
            MockMvcRequestBuilders.post("$PATH/{behandlingId}/brev", behandlingId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $jwt")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(Charsets.UTF_8)
                .content(brevSomStreng)
        ).andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isAccepted)
    }

    @Test
    fun `send tannlegeerklæring veiledningsbrev - ikkeeksisterende behandlingId`() {
        val brevSomStreng = hentTannlegeerklaeringVeiledningbrev()
        val ikkeeksisterendeBehandlingId = 999999

        mvc.perform(
            MockMvcRequestBuilders.post("$PATH/{behandlingId}/brev", ikkeeksisterendeBehandlingId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $jwt")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(Charsets.UTF_8)
                .content(brevSomStreng)
        ).andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().is4xxClientError)
    }

    private fun hentTannlegeerklaeringVeiledningbrev(): String {
        val brevSomStreng = tannlegeerklaeringVeiledningbrev()
        val brev = jacksonObjectMapper().readValue(brevSomStreng, Brev::class.java)
        Assertions.assertThat(brev).isNotNull
        return brevSomStreng
    }
}