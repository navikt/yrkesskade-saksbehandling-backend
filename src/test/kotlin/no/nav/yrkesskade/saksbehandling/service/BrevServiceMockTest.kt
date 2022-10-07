package no.nav.yrkesskade.saksbehandling.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import no.nav.yrkesskade.saksbehandling.client.BrevutsendingClient
import no.nav.yrkesskade.saksbehandling.client.JsonToPdfClient
import no.nav.yrkesskade.saksbehandling.fixtures.behandlingsstatus
import no.nav.yrkesskade.saksbehandling.fixtures.behandlingstyper
import no.nav.yrkesskade.saksbehandling.fixtures.dokumentkategori
import no.nav.yrkesskade.saksbehandling.fixtures.framdriftsstatus
import no.nav.yrkesskade.saksbehandling.fixtures.genererBehandling
import no.nav.yrkesskade.saksbehandling.fixtures.genererSak
import no.nav.yrkesskade.saksbehandling.model.Behandlingsstatus
import no.nav.yrkesskade.saksbehandling.model.Brev
import no.nav.yrkesskade.saksbehandling.util.MDCConstants
import no.nav.yrkesskade.saksbehandling.util.kodeverk.KodeverdiMapper
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.MDC
import tannlegeerklaeringVeiledningbrev
import java.util.UUID

@ExtendWith(MockKExtension::class)
internal class BrevServiceMockTest {

    private val brevutsendingClientMock: BrevutsendingClient = mockk()
    private val jsonToPdfClientMock: JsonToPdfClient = mockk()
    private val behandlingServiceMock: BehandlingService = mockk()
    private val kodeverkServiceMock: KodeverkService = mockk()

    private val brev = jacksonObjectMapper().readValue(tannlegeerklaeringVeiledningbrev(), Brev::class.java)

    private lateinit var brevService: BrevService

    @BeforeEach
    fun setup() {
        MDC.put(MDCConstants.MDC_CALL_ID, UUID.randomUUID().toString())
        every { behandlingServiceMock.hentBehandling(eq(1)) } answers {
            genererBehandling(1, "123", Behandlingsstatus.FERDIG, genererSak())
        }
        justRun { brevutsendingClientMock.sendTilBrevutsending(any()) }
        mockKodeverk()
        brevService  = BrevService(
            brevutsendingClient = brevutsendingClientMock,
            jsonToPdfClient = jsonToPdfClientMock,
            behandlingService = behandlingServiceMock,
            kodeverkService = kodeverkServiceMock
        )
    }

    private fun mockKodeverk() {
        every { kodeverkServiceMock.hentKodeverk("behandlingstype", null) } returns behandlingstyper()
        every { kodeverkServiceMock.hentKodeverk("behandlingsstatus", null) } returns behandlingsstatus()
        every { kodeverkServiceMock.hentKodeverk("framdriftsstatus", null) } returns framdriftsstatus()
        every { kodeverkServiceMock.hentKodeverk("dokumenttype", null) } returns dokumentkategori()
    }

    @Test
    fun `sendTilBrevutsending - behandling med FNR`() {
        brevService.sendTilBrevutsending(1, brev)
        verify(exactly = 1) { brevutsendingClientMock.sendTilBrevutsending(any()) }
    }
}