package no.nav.yrkesskade.saksbehandling.service

import io.mockk.every
import io.mockk.mockk
import no.nav.yrkesskade.saksbehandling.fixtures.dokumentTilSaksbehandlingHendelse
import no.nav.yrkesskade.saksbehandling.fixtures.journalpostResultWithBrukerAktoerid
import no.nav.yrkesskade.saksbehandling.graphql.client.SafClient
import no.nav.yrkesskade.saksbehandling.model.DokumentTilSaksbehandling
import no.nav.yrkesskade.saksbehandling.model.DokumentTilSaksbehandlingMetadata
import no.nav.yrkesskade.saksbehandling.repository.BehandlingRepository
import no.nav.yrkesskade.saksbehandling.test.AbstractTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.BeforeEach
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

    @BeforeEach
    fun setup() {
        dokumentmottak = Dokumentmottak(
            behandlingService = behandlingService,
            sakService = sakService,
            safClient = safClientMock
        )
    }

    @Test
    fun mottaDokument() {
        every { safClientMock.hentOppdatertJournalpost(any()) } returns journalpostResultWithBrukerAktoerid()
        dokumentmottak.mottaDokument(dokumentTilSaksbehandlingHendelse())
        assertThat(behandlingRepository.findAll().size).isEqualTo(1)
    }
}