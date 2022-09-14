package no.nav.yrkesskade.saksbehandling.service

import com.expediagroup.graphql.generated.enums.BrukerIdType
import com.expediagroup.graphql.generated.enums.IdentGruppe
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import no.nav.yrkesskade.meldingmottak.fixtures.hentIdenterResultMedFnrUtenHistorikk
import no.nav.yrkesskade.saksbehandling.client.BrevutsendingClient
import no.nav.yrkesskade.saksbehandling.client.JsonToPdfClient
import no.nav.yrkesskade.saksbehandling.fixtures.genererBehandling
import no.nav.yrkesskade.saksbehandling.fixtures.genererSak
import no.nav.yrkesskade.saksbehandling.graphql.client.pdl.PdlClient
import no.nav.yrkesskade.saksbehandling.model.Behandlingsstatus
import no.nav.yrkesskade.saksbehandling.model.Brev
import no.nav.yrkesskade.saksbehandling.util.MDCConstants
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.MDC
import tannlegeerklaeringVeiledningbrev
import java.lang.RuntimeException
import java.util.UUID

@ExtendWith(MockKExtension::class)
internal class BrevServiceTest {

    private val brevutsendingClientMock: BrevutsendingClient = mockk()
    private val jsonToPdfClientMock: JsonToPdfClient = mockk()
    private val pdlClientMock: PdlClient = mockk()
    private val behandlingServiceMock: BehandlingService = mockk()

    private val brevService = BrevService(
        brevutsendingClient = brevutsendingClientMock,
        jsonToPdfClient = jsonToPdfClientMock,
        pdlClient = pdlClientMock,
        behandlingService = behandlingServiceMock
    )

    @BeforeEach
    fun setup() {
        MDC.put(MDCConstants.MDC_CALL_ID, UUID.randomUUID().toString())
        every { behandlingServiceMock.hentBehandling(eq(1)) } answers {
            genererBehandling(1, "123", Behandlingsstatus.FERDIG, genererSak())
        }
        every { pdlClientMock.hentIdenter(eq("12345"), eq(listOf(IdentGruppe.FOLKEREGISTERIDENT)), eq(false)) } answers {
            hentIdenterResultMedFnrUtenHistorikk()
        }
        justRun { brevutsendingClientMock.sendTilBrevutsending(any()) }
    }

    @Test
    fun sendTilBrevutsending() {
        val brev = jacksonObjectMapper().readValue(tannlegeerklaeringVeiledningbrev(), Brev::class.java)
        brevService.sendTilBrevutsending(1, brev)

        verify(exactly = 1) { brevutsendingClientMock.sendTilBrevutsending(any()) }
    }

    @Test
    fun `sendTilBrevutsending feiler naar brukerIdType ikke er FNR eller AKTOERID`() {
        every { behandlingServiceMock.hentBehandling(eq(1)) } answers {
            genererBehandling(1, "123", Behandlingsstatus.FERDIG, genererSak()).copy(
                brukerIdType = BrukerIdType.ORGNR
            )
        }
        val brev = jacksonObjectMapper().readValue(tannlegeerklaeringVeiledningbrev(), Brev::class.java)

        val runtimeException = assertThrows<RuntimeException> {
            brevService.sendTilBrevutsending(1, brev)
        }
        assertThat(runtimeException.localizedMessage).isEqualTo("Utsending av brev for brukerIdType ${BrukerIdType.ORGNR.name} støttes ikke")

    }
}