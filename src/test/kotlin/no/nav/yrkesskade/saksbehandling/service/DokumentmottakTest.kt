package no.nav.yrkesskade.saksbehandling.service

import io.mockk.every
import io.mockk.mockk
import no.nav.yrkesskade.saksbehandling.client.BrevutsendingClient
import no.nav.yrkesskade.saksbehandling.fixtures.dokumentTilSaksbehandlingHendelse
import no.nav.yrkesskade.saksbehandling.fixtures.journalpostResultWithBrukerAktoerid
import no.nav.yrkesskade.saksbehandling.graphql.client.SafClient
import no.nav.yrkesskade.saksbehandling.repository.BehandlingRepository
import no.nav.yrkesskade.saksbehandling.test.AbstractTest
import no.nav.yrkesskade.saksbehandling.util.MDCConstants
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.BeforeEach
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID


class DokumentmottakTest : AbstractTest() {

    lateinit var dokumentmottak: Dokumentmottak

    @Autowired
    lateinit var behandlingService: BehandlingService

    @Autowired
    lateinit var behandlingRepository: BehandlingRepository

    @Autowired
    lateinit var sakService: SakService

    private val safClientMock: SafClient = mockk()

    @Autowired
    lateinit var brevutsendingClient: BrevutsendingClient

    @BeforeEach
    fun setup() {
        MDC.put(MDCConstants.MDC_CALL_ID, UUID.randomUUID().toString())
        dokumentmottak = Dokumentmottak(
            behandlingService = behandlingService,
            sakService = sakService,
            safClient = safClientMock,
            brevutsendingClient = brevutsendingClient
        )
    }

    @Test
    fun mottaDokument() {
        every { safClientMock.hentOppdatertJournalpost(any()) } returns journalpostResultWithBrukerAktoerid()
        dokumentmottak.mottaDokument(dokumentTilSaksbehandlingHendelse())
        assertThat(behandlingRepository.findAll().size).isEqualTo(1)
    }
}