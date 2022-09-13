package no.nav.yrkesskade.saksbehandling.repository

import no.nav.yrkesskade.saksbehandling.fixtures.genererBehandling
import no.nav.yrkesskade.saksbehandling.fixtures.genererSak
import no.nav.yrkesskade.saksbehandling.model.Behandlingsstatus
import no.nav.yrkesskade.saksbehandling.model.Behandlingstype
import no.nav.yrkesskade.saksbehandling.test.AbstractTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.transaction.annotation.Transactional

class BehandlingRepositoryTest : AbstractTest() {

    @Autowired
    lateinit var behandlingRepository: BehandlingRepository

    @Autowired
    lateinit var sakRepository: SakRepository

    @BeforeEach
    fun setUp() {
        resetDatabase()
        sakRepository.save(genererSak())
    }

    @Transactional
    fun resetDatabase() {
        behandlingRepository.deleteAll()
        sakRepository.deleteAll()
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
        behandlingRepository.save(genererBehandling(2L, "test", Behandlingsstatus.IKKE_PAABEGYNT, sak))
        behandlingRepository.save(genererBehandling(30L, "test", Behandlingsstatus.FERDIG, sak))
        val behandling = genererBehandling(1L, "test", Behandlingsstatus.UNDER_BEHANDLING, sak)

        behandlingRepository.save(behandling)

        val behandlinger = behandlingRepository.findBySaksbehandlingsansvarligIdentAndStatus("test", Behandlingsstatus.FERDIG, Pageable.ofSize(10))
        assertThat(behandlinger.numberOfElements).isEqualTo(1)
    }

    @Test
    fun `hent egne behandlinger - under behandling`() {
        val sak = sakRepository.findAll().first()
        val behandling = genererBehandling(1L, "test", Behandlingsstatus.UNDER_BEHANDLING, sak)

        behandlingRepository.save(behandling)

        val behandlinger = behandlingRepository.findBySaksbehandlingsansvarligIdentAndStatus("test", Behandlingsstatus.UNDER_BEHANDLING, Pageable.ofSize(10))
        assertThat(behandlinger.numberOfElements).isEqualTo(1)
    }

    @Test
    fun `hent egne behandlinger - ingen treff`() {
        val sak = sakRepository.findAll().first()
        val behandling = genererBehandling(1L, "todd", Behandlingsstatus.IKKE_PAABEGYNT, sak)

        behandlingRepository.save(behandling)

        val behandlinger = behandlingRepository.findBySaksbehandlingsansvarligIdentAndStatus("test", Behandlingsstatus.UNDER_BEHANDLING, Pageable.ofSize(10))
        assertThat(behandlinger.numberOfElements).isEqualTo(0)
    }

    @Test
    fun `hent aapne behandlinger med alle filter`() {
        // given
        val sak = sakRepository.findAll().first()
        val ikkePaabegyntBehandling = genererBehandling(1L, null, Behandlingsstatus.IKKE_PAABEGYNT, sak)
        behandlingRepository.save(ikkePaabegyntBehandling)
        val underBehandlingBehandling = genererBehandling(2L, "test", Behandlingsstatus.UNDER_BEHANDLING, sak)
        behandlingRepository.save(underBehandlingBehandling)
        val ferdigBehandling = genererBehandling(3L, "test", Behandlingsstatus.FERDIG, sak)
        behandlingRepository.save(ferdigBehandling)

        //when
        val behandlinger = behandlingRepository.findBehandlingerBegrensetTilBehandlingsstatuser(Behandlingsstatus.UNDER_BEHANDLING, "enFinKategori", Behandlingstype.VEILEDNING, listOf(Behandlingsstatus.UNDER_BEHANDLING, Behandlingsstatus.IKKE_PAABEGYNT), Pageable.unpaged())

        // then
        assertThat(behandlinger.numberOfElements).isEqualTo(1)
    }

    @Test
    fun `hent aapne behandlinger med status filter`() {
        // given
        val sak = sakRepository.findAll().first()
        val ikkePaabegyntBehandling = genererBehandling(1L, null, Behandlingsstatus.IKKE_PAABEGYNT, sak)
        behandlingRepository.save(ikkePaabegyntBehandling)
        val underBehandlingBehandling = genererBehandling(2L, "test", Behandlingsstatus.UNDER_BEHANDLING, sak)
        behandlingRepository.save(underBehandlingBehandling)
        val ferdigBehandling = genererBehandling(3L, "test", Behandlingsstatus.FERDIG, sak)
        behandlingRepository.save(ferdigBehandling)

        //when
        val behandlinger = behandlingRepository.findBehandlingerBegrensetTilBehandlingsstatuser(Behandlingsstatus.UNDER_BEHANDLING, null, null, listOf(Behandlingsstatus.UNDER_BEHANDLING, Behandlingsstatus.IKKE_PAABEGYNT), Pageable.unpaged())

        // then
        assertThat(behandlinger.numberOfElements).isEqualTo(1)
    }

    @Test
    fun `hent aapne behandlinger med dokumentkategori filter`() {
        // given
        val sak = sakRepository.findAll().first()
        val ikkePaabegyntBehandling = genererBehandling(100L, null, Behandlingsstatus.IKKE_PAABEGYNT, sak)
        behandlingRepository.save(ikkePaabegyntBehandling)
        val underBehandlingBehandling = genererBehandling(200L, "test", Behandlingsstatus.UNDER_BEHANDLING, sak)
        behandlingRepository.save(underBehandlingBehandling)
        val ferdigBehandling = genererBehandling(300L, "test", Behandlingsstatus.FERDIG, sak)
        behandlingRepository.save(ferdigBehandling)

        //when
        val behandlinger = behandlingRepository.findBehandlingerBegrensetTilBehandlingsstatuser(null, "enFinKategori", null, listOf(Behandlingsstatus.UNDER_BEHANDLING, Behandlingsstatus.IKKE_PAABEGYNT), Pageable.unpaged())

        // then
        assertThat(behandlinger.numberOfElements).isEqualTo(2)
    }

    @Test
    fun `hent aapne behandlinger med behandlingstype VEILEDNING filter`() {
        // given
        val sak = sakRepository.findAll().first()
        val ikkePaabegyntBehandling = genererBehandling(51L, null, Behandlingsstatus.IKKE_PAABEGYNT, sak)
        behandlingRepository.save(ikkePaabegyntBehandling)
        val underBehandlingBehandling = genererBehandling(52L, "test", Behandlingsstatus.UNDER_BEHANDLING, sak)
        behandlingRepository.save(underBehandlingBehandling)
        val ferdigBehandling = genererBehandling(53L, "test", Behandlingsstatus.FERDIG, sak)
        behandlingRepository.save(ferdigBehandling)

        //when
        val behandlinger = behandlingRepository.findBehandlingerBegrensetTilBehandlingsstatuser(null, null, Behandlingstype.VEILEDNING, listOf(Behandlingsstatus.UNDER_BEHANDLING, Behandlingsstatus.IKKE_PAABEGYNT), Pageable.unpaged())

        // then
        assertThat(behandlinger.numberOfElements).isEqualTo(2)
    }

    @Test
    fun `hent aapne behandlinger med behandlingstype JOURNALFORING filter`() {
        // given
        val sak = sakRepository.findAll().first()
        val ikkePaabegyntBehandling = genererBehandling(51L, null, Behandlingsstatus.IKKE_PAABEGYNT, sak)
        behandlingRepository.save(ikkePaabegyntBehandling)
        val underBehandlingBehandling = genererBehandling(52L, "test", Behandlingsstatus.UNDER_BEHANDLING, sak, Behandlingstype.JOURNALFOERING)
        behandlingRepository.save(underBehandlingBehandling)
        val ferdigBehandling = genererBehandling(53L, "test", Behandlingsstatus.FERDIG, sak, Behandlingstype.JOURNALFOERING)
        behandlingRepository.save(ferdigBehandling)

        //when
        val behandlinger = behandlingRepository.findBehandlingerBegrensetTilBehandlingsstatuser(null, null, Behandlingstype.VEILEDNING, listOf(Behandlingsstatus.UNDER_BEHANDLING, Behandlingsstatus.IKKE_PAABEGYNT), Pageable.unpaged())

        // then
        assertThat(behandlinger.size).isEqualTo(1)
    }

    @Test
    fun `hent aapne behandlinger med uten filter`() {
        // given
        val sak = sakRepository.findAll().first()
        val ikkePaabegyntBehandling = genererBehandling(1L, null, Behandlingsstatus.IKKE_PAABEGYNT, sak)
        behandlingRepository.save(ikkePaabegyntBehandling)
        val underBehandlingBehandling = genererBehandling(2L, "test", Behandlingsstatus.UNDER_BEHANDLING, sak)
        behandlingRepository.save(underBehandlingBehandling)
        val ferdigBehandling = genererBehandling(3L, "test", Behandlingsstatus.FERDIG, sak)
        behandlingRepository.save(ferdigBehandling)

        //when
        val behandlinger = behandlingRepository.findBehandlingerBegrensetTilBehandlingsstatuser(null, null, null, listOf(Behandlingsstatus.UNDER_BEHANDLING, Behandlingsstatus.IKKE_PAABEGYNT), Pageable.unpaged())

        // then
        assertThat(behandlinger.numberOfElements).isEqualTo(2)
    }
}