package no.nav.yrkesskade.saksbehandling.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.yrkesskade.saksbehandling.client.bigquery.BigQueryClient
import no.nav.yrkesskade.saksbehandling.fixtures.dokumentTilSaksbehandlingHendelse
import no.nav.yrkesskade.saksbehandling.fixtures.journalpost.journalpostResultTannlegeerklaeringWithBrukerAktoerid
import no.nav.yrkesskade.saksbehandling.fixtures.journalpost.journalpostResultTannlegeerklaeringWithBrukerFnr
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

    private val safClientMock: SafClient = mockk()

    private val pdlServiceMock: PdlService = mockk()

    @Autowired
    lateinit var behandlingRepository: BehandlingRepository

    @Autowired
    lateinit var bigQueryClient: BigQueryClient

    @BeforeEach
    fun setup() {
        MDC.put(MDCConstants.MDC_CALL_ID, UUID.randomUUID().toString())
        dokumentmottak = Dokumentmottak(
            behandlingService = behandlingService,
            safClient = safClientMock,
            pdlService = pdlServiceMock,
            bigQueryClient = bigQueryClient
        )
        every { pdlServiceMock.hentFoedselsnummerMedMaskinTilMaskinToken(any()) } returns "01010112345"
        every { safClientMock.hentOppdatertJournalpost(any()) } returns journalpostResultTannlegeerklaeringWithBrukerAktoerid()
        klargjorDatabase()
    }

    @Transactional
    fun klargjorDatabase() {
        behandlingRepository.deleteAll()
    }

    @Test
    fun mottaTannlegeerklaering() {
        dokumentmottak.mottaDokument(dokumentTilSaksbehandlingHendelse())

        val behandlingEntities = behandlingRepository.findAll()
        assertThat(behandlingEntities.size).isEqualTo(1)

        val behandlingEntity = behandlingEntities.first()
        assertThat(behandlingEntity.behandlendeEnhet).isEqualTo(dokumentTilSaksbehandlingHendelse().dokumentTilSaksbehandling.enhet)
        assertThat(behandlingEntity.behandlingstype).isEqualTo(Behandlingstype.JOURNALFOERING)
        assertThat(behandlingEntity.dokumentkategori).isEqualTo("tannlegeerklaering")
    }

    @Test
    fun `mottaDokument skal hente FNR når journalpost bruker aktørId`() {
        dokumentmottak.mottaDokument(dokumentTilSaksbehandlingHendelse())

        verify(exactly = 1) { pdlServiceMock.hentFoedselsnummerMedMaskinTilMaskinToken(any()) }
    }

    @Test
    fun `mottaDokument skal ikke hente FNR når journalpost bruker FNR`() {
        every { safClientMock.hentOppdatertJournalpost(any()) } returns journalpostResultTannlegeerklaeringWithBrukerFnr()

        dokumentmottak.mottaDokument(dokumentTilSaksbehandlingHendelse())

        verify(exactly = 0) { pdlServiceMock.hentFoedselsnummerMedMaskinTilMaskinToken(any()) }
    }
}