package no.nav.yrkesskade.saksbehandling.service

import io.mockk.every
import io.mockk.mockk
import no.nav.yrkesskade.saksbehandling.client.BrevutsendingClient
import no.nav.yrkesskade.saksbehandling.client.bigquery.BigQueryClient
import no.nav.yrkesskade.saksbehandling.fixtures.dokumentTilSaksbehandlingHendelse
import no.nav.yrkesskade.saksbehandling.fixtures.journalpost.journalpostResultTannlegeerklaeringWithBrukerAktoerid
import no.nav.yrkesskade.saksbehandling.graphql.client.saf.SafClient
import no.nav.yrkesskade.saksbehandling.model.Behandlingstype
import no.nav.yrkesskade.saksbehandling.repository.BehandlingRepository
import no.nav.yrkesskade.saksbehandling.test.AbstractTest
import no.nav.yrkesskade.saksbehandling.util.MDCConstants
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import java.util.*


class DokumentmottakTest : AbstractTest() {

    lateinit var dokumentmottak: Dokumentmottak

    @Autowired
    lateinit var behandlingService: BehandlingService

    @Autowired
    lateinit var sakService: SakService

    private val safClientMock: SafClient = mockk()

    @Autowired
    lateinit var brevutsendingClient: BrevutsendingClient

    @Autowired
    lateinit var behandlingRepository: BehandlingRepository

    @Autowired
    lateinit var bigQueryClient: BigQueryClient

    @BeforeEach
    fun setup() {
        MDC.put(MDCConstants.MDC_CALL_ID, UUID.randomUUID().toString())
        dokumentmottak = Dokumentmottak(
            behandlingService = behandlingService,
            sakService = sakService,
            safClient = safClientMock,
            brevutsendingClient = brevutsendingClient,
            bigQueryClient = bigQueryClient
        )
        klargjorDatabase()
    }

    @Transactional
    fun klargjorDatabase() {
        behandlingRepository.deleteAll()
    }

    @Test
    fun mottaTannlegeerklaering() {
        every { safClientMock.hentOppdatertJournalpost(any()) } returns journalpostResultTannlegeerklaeringWithBrukerAktoerid()
        dokumentmottak.mottaDokument(dokumentTilSaksbehandlingHendelse())

        val behandlingEntities = behandlingRepository.findAll()
        assertThat(behandlingEntities.size).isEqualTo(1)

        val behandlingEntity = behandlingEntities.first()
        assertThat(behandlingEntity.behandlendeEnhet).isEqualTo(dokumentTilSaksbehandlingHendelse().dokumentTilSaksbehandling.enhet)
        assertThat(behandlingEntity.behandlingstype).isEqualTo(Behandlingstype.JOURNALFOERING)
        assertThat(behandlingEntity.dokumentkategori).isEqualTo("tannlegeerklaering")
    }

}