package no.nav.yrkesskade.saksbehandling.repository

import no.nav.yrkesskade.saksbehandling.fixtures.behandlingsstatus
import no.nav.yrkesskade.saksbehandling.fixtures.behandlingstyper
import no.nav.yrkesskade.saksbehandling.fixtures.dokumentkategori
import no.nav.yrkesskade.saksbehandling.fixtures.framdriftsstatus
import no.nav.yrkesskade.saksbehandling.fixtures.genererBehandling
import no.nav.yrkesskade.saksbehandling.fixtures.genererSak
import no.nav.yrkesskade.saksbehandling.model.Behandlingsstatus
import no.nav.yrkesskade.saksbehandling.model.Behandlingstype
import no.nav.yrkesskade.saksbehandling.service.KodeverkService
import no.nav.yrkesskade.saksbehandling.test.AbstractTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.Pageable
import org.springframework.transaction.annotation.Transactional

class BehandlingRepositoryTest : AbstractTest() {

    @Autowired
    lateinit var behandlingRepository: BehandlingRepository

    @Autowired
    lateinit var sakRepository: SakRepository

    @MockBean
    lateinit var kodeverkService: KodeverkService

    @BeforeEach
    fun setUp() {
        mockKodeverk()
        resetDatabase()
        sakRepository.save(genererSak())
    }

    @Transactional
    fun resetDatabase() {
        behandlingRepository.deleteAll()
        sakRepository.deleteAll()
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
    fun `hent egne behandlinger`() {
        val sak = sakRepository.findAll().first()
        behandlingRepository.save(genererBehandling(2L, "test", Behandlingsstatus.IKKE_PAABEGYNT, sak))
        behandlingRepository.save(genererBehandling(3L, "test", Behandlingsstatus.FERDIG, sak))
        behandlingRepository.save(genererBehandling(1L, "test", Behandlingsstatus.UNDER_BEHANDLING, sak))

        assertThat(behandlingRepository.count()).isEqualTo(3)
        val behandlinger = behandlingRepository.findBySaksbehandlingsansvarligIdentAndStatus("test", Behandlingsstatus.UNDER_BEHANDLING, Pageable.ofSize(10))
        assertThat(behandlinger.numberOfElements).isEqualTo(1)
        val behandling = behandlinger.first()
        assertThat(behandling.status).isEqualTo(Behandlingsstatus.UNDER_BEHANDLING)
    }

    @Test
    fun `hent egne behandlinger - ferdig`() {
        val sak = sakRepository.findAll().first()
        behandlingRepository.save(genererBehandling(4L, "test", Behandlingsstatus.IKKE_PAABEGYNT, sak))
        behandlingRepository.save(genererBehandling(5L, "test", Behandlingsstatus.FERDIG, sak))
        val behandling = genererBehandling(6L, "test", Behandlingsstatus.UNDER_BEHANDLING, sak)

        behandlingRepository.save(behandling)

        val behandlinger = behandlingRepository.findBySaksbehandlingsansvarligIdentAndStatus("test", Behandlingsstatus.FERDIG, Pageable.ofSize(10))
        assertThat(behandlinger.numberOfElements).isEqualTo(1)
    }

    @Test
    fun `hent egne behandlinger - under behandling`() {
        val sak = sakRepository.findAll().first()
        val behandling = genererBehandling(7L, "test", Behandlingsstatus.UNDER_BEHANDLING, sak)

        behandlingRepository.save(behandling)

        val behandlinger = behandlingRepository.findBySaksbehandlingsansvarligIdentAndStatus("test", Behandlingsstatus.UNDER_BEHANDLING, Pageable.ofSize(10))
        assertThat(behandlinger.numberOfElements).isEqualTo(1)
    }

    @Test
    fun `hent egne behandlinger - ingen treff`() {
        val sak = sakRepository.findAll().first()
        val behandling = genererBehandling(8L, "todd", Behandlingsstatus.IKKE_PAABEGYNT, sak)

        behandlingRepository.save(behandling)

        val behandlinger = behandlingRepository.findBySaksbehandlingsansvarligIdentAndStatus("test", Behandlingsstatus.UNDER_BEHANDLING, Pageable.ofSize(10))
        assertThat(behandlinger.numberOfElements).isEqualTo(0)
    }

    @Test
    fun `hent aapne behandlinger med alle filter`() {
        // given
        val sak = sakRepository.findAll().first()
        behandlingRepository.save(genererBehandling(9L, null, Behandlingsstatus.IKKE_PAABEGYNT, sak))
        behandlingRepository.save(genererBehandling(10L, "test", Behandlingsstatus.UNDER_BEHANDLING, sak))
        behandlingRepository.save(genererBehandling(11L, null, Behandlingsstatus.UNDER_BEHANDLING, sak))
        behandlingRepository.save(genererBehandling(12L, "test", Behandlingsstatus.FERDIG, sak))

        //when
        val behandlinger = behandlingRepository.findBehandlingerBegrensetTilBehandlingsstatuser(Behandlingsstatus.UNDER_BEHANDLING, "enFinKategori", Behandlingstype.VEILEDNING, listOf(Behandlingsstatus.UNDER_BEHANDLING, Behandlingsstatus.IKKE_PAABEGYNT), false, Pageable.unpaged())

        // then
        assertThat(behandlinger.numberOfElements).isEqualTo(1)
    }

    @Test
    fun `hent aapne behandlinger med status filter`() {
        // given
        val sak = sakRepository.findAll().first()
        behandlingRepository.save(genererBehandling(13L, null, Behandlingsstatus.IKKE_PAABEGYNT, sak))
        behandlingRepository.save(genererBehandling(14L, "test", Behandlingsstatus.UNDER_BEHANDLING, sak))
        behandlingRepository.save(genererBehandling(15L, null, Behandlingsstatus.UNDER_BEHANDLING, sak))
        behandlingRepository.save(genererBehandling(16L, "test", Behandlingsstatus.FERDIG, sak))

        //when
        val behandlinger = behandlingRepository.findBehandlingerBegrensetTilBehandlingsstatuser(Behandlingsstatus.UNDER_BEHANDLING, null, null, listOf(Behandlingsstatus.UNDER_BEHANDLING, Behandlingsstatus.IKKE_PAABEGYNT), false, Pageable.unpaged())

        // then
        assertThat(behandlinger.numberOfElements).isEqualTo(1)
    }

    @Test
    fun `hent aapne behandlinger med dokumentkategori filter`() {
        // given
        val sak = sakRepository.findAll().first()
        behandlingRepository.save(genererBehandling(17L, null, Behandlingsstatus.IKKE_PAABEGYNT, sak))
        behandlingRepository.save(genererBehandling(18L, "test", Behandlingsstatus.UNDER_BEHANDLING, sak))
        behandlingRepository.save(genererBehandling(19L, null, Behandlingsstatus.UNDER_BEHANDLING, sak))
        behandlingRepository.save(genererBehandling(20L, "test", Behandlingsstatus.FERDIG, sak))

        //when
        val behandlinger = behandlingRepository.findBehandlingerBegrensetTilBehandlingsstatuser(null, "enFinKategori", null, listOf(Behandlingsstatus.UNDER_BEHANDLING, Behandlingsstatus.IKKE_PAABEGYNT), false, Pageable.unpaged())

        // then
        assertThat(behandlinger.numberOfElements).isEqualTo(2)
    }

    @Test
    fun `hent aapne behandlinger med behandlingstype VEILEDNING filter`() {
        // given
        val sak = sakRepository.findAll().first()
        behandlingRepository.save(genererBehandling(21L, null, Behandlingsstatus.IKKE_PAABEGYNT, sak))
        behandlingRepository.save(genererBehandling(22L, "test", Behandlingsstatus.UNDER_BEHANDLING, sak))
        behandlingRepository.save(genererBehandling(24L, null, Behandlingsstatus.UNDER_BEHANDLING, sak))
        behandlingRepository.save(genererBehandling(23L, "test", Behandlingsstatus.FERDIG, sak))

        //when
        val behandlinger = behandlingRepository.findBehandlingerBegrensetTilBehandlingsstatuser(null, null, Behandlingstype.VEILEDNING, listOf(Behandlingsstatus.UNDER_BEHANDLING, Behandlingsstatus.IKKE_PAABEGYNT), false, Pageable.unpaged())

        // then
        assertThat(behandlinger.numberOfElements).isEqualTo(2)
    }

    @Test
    fun `hent aapne behandlinger med behandlingstype VEILEDNING filter inkludert behandlinger med saksbehandler`() {
        // given
        val sak = sakRepository.findAll().first()
        behandlingRepository.save(genererBehandling(25L, null, Behandlingsstatus.IKKE_PAABEGYNT, sak))
        behandlingRepository.save(genererBehandling(26L, "test", Behandlingsstatus.UNDER_BEHANDLING, sak))
        behandlingRepository.save(genererBehandling(27L, null, Behandlingsstatus.UNDER_BEHANDLING, sak))
        behandlingRepository.save(genererBehandling(28L, "test", Behandlingsstatus.FERDIG, sak))

        //when
        val behandlinger = behandlingRepository.findBehandlingerBegrensetTilBehandlingsstatuser(null, null, Behandlingstype.VEILEDNING, listOf(Behandlingsstatus.UNDER_BEHANDLING, Behandlingsstatus.IKKE_PAABEGYNT), true, Pageable.unpaged())

        // then
        assertThat(behandlinger.numberOfElements).isEqualTo(3)
    }

    @Test
    fun `hent aapne behandlinger med behandlingstype JOURNALFORING filter`() {
        // given
        val sak = sakRepository.findAll().first()
        behandlingRepository.save(genererBehandling(29L, null, Behandlingsstatus.IKKE_PAABEGYNT, sak))
        behandlingRepository.save(genererBehandling(30L, "test", Behandlingsstatus.UNDER_BEHANDLING, sak, Behandlingstype.JOURNALFOERING))
        behandlingRepository.save(genererBehandling(31L, "test", Behandlingsstatus.FERDIG, sak, Behandlingstype.JOURNALFOERING))

        //when
        val behandlinger = behandlingRepository.findBehandlingerBegrensetTilBehandlingsstatuser(null, null, Behandlingstype.VEILEDNING, listOf(Behandlingsstatus.UNDER_BEHANDLING, Behandlingsstatus.IKKE_PAABEGYNT), false, Pageable.unpaged())

        // then
        assertThat(behandlinger.size).isEqualTo(1)
    }

    @Test
    fun `hent aapne behandlinger med uten filter`() {
        // given
        val sak = sakRepository.findAll().first()
        behandlingRepository.save(genererBehandling(32L, null, Behandlingsstatus.IKKE_PAABEGYNT, sak))
        behandlingRepository.save(genererBehandling(33L, "test", Behandlingsstatus.UNDER_BEHANDLING, sak))
        behandlingRepository.save(genererBehandling(34L, null, Behandlingsstatus.UNDER_BEHANDLING, sak))
        behandlingRepository.save(genererBehandling(35L, "test", Behandlingsstatus.FERDIG, sak))

        //when
        val behandlinger = behandlingRepository.findBehandlingerBegrensetTilBehandlingsstatuser(null, null, null, listOf(Behandlingsstatus.UNDER_BEHANDLING, Behandlingsstatus.IKKE_PAABEGYNT), false, Pageable.unpaged())

        // then
        assertThat(behandlinger.numberOfElements).isEqualTo(2)
    }
}