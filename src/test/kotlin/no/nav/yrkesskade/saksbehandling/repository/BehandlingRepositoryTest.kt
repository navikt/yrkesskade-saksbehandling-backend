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
        genererBehandling(2L, "test", Behandlingsstatus.IKKE_PAABEGYNT, sak)
        val behandling = genererBehandling(1L, "test", Behandlingsstatus.UNDER_BEHANDLING, sak)

        behandlingRepository.save(behandling)

        val behandlinger = behandlingRepository.findBySaksbehandlingsansvarligIdent("test", Pageable.ofSize(10))
        assertThat(behandlinger.size).isEqualTo(1)
    }

    @Test
    fun `hent egne behandlinger - under behandling`() {
        val sak = sakRepository.findAll().first()
        val behandling = genererBehandling(1L, "test", Behandlingsstatus.UNDER_BEHANDLING, sak)

        behandlingRepository.save(behandling)

        val behandlinger = behandlingRepository.findBySaksbehandlingsansvarligIdent("test", Pageable.ofSize(10))
        assertThat(behandlinger.size).isEqualTo(1)
    }

    @Test
    fun `hent egne behandlinger - ingen treff`() {
        val sak = sakRepository.findAll().first()
        val behandling = genererBehandling(1L, "todd", Behandlingsstatus.IKKE_PAABEGYNT, sak)

        behandlingRepository.save(behandling)

        val behandlinger = behandlingRepository.findBySaksbehandlingsansvarligIdent("test", Pageable.ofSize(10))
        assertThat(behandlinger.size).isEqualTo(0)
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
        assertThat(behandlinger.size).isEqualTo(1)
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
        assertThat(behandlinger.size).isEqualTo(1)
    }

    @Test
    fun `hent aapne behandlinger med dokumentkategori filter`() {
        // given
        val sak = sakRepository.findAll().first()
        val ikkePaabegyntBehandling = genererBehandling(1L, null, Behandlingsstatus.IKKE_PAABEGYNT, sak)
        behandlingRepository.save(ikkePaabegyntBehandling)
        val underBehandlingBehandling = genererBehandling(2L, "test", Behandlingsstatus.UNDER_BEHANDLING, sak)
        behandlingRepository.save(underBehandlingBehandling)
        val ferdigBehandling = genererBehandling(3L, "test", Behandlingsstatus.FERDIG, sak)
        behandlingRepository.save(ferdigBehandling)

        //when
        val behandlinger = behandlingRepository.findBehandlingerBegrensetTilBehandlingsstatuser(null, "enFinKategori", null, listOf(Behandlingsstatus.UNDER_BEHANDLING, Behandlingsstatus.IKKE_PAABEGYNT), Pageable.unpaged())

        // then
        assertThat(behandlinger.size).isEqualTo(2)
    }

    @Test
    fun `hent aapne behandlinger med behandlingstype filter`() {
        // given
        val sak = sakRepository.findAll().first()
        val ikkePaabegyntBehandling = genererBehandling(1L, null, Behandlingsstatus.IKKE_PAABEGYNT, sak)
        behandlingRepository.save(ikkePaabegyntBehandling)
        val underBehandlingBehandling = genererBehandling(2L, "test", Behandlingsstatus.UNDER_BEHANDLING, sak)
        behandlingRepository.save(underBehandlingBehandling)
        val ferdigBehandling = genererBehandling(3L, "test", Behandlingsstatus.FERDIG, sak)
        behandlingRepository.save(ferdigBehandling)

        //when
        val behandlinger = behandlingRepository.findBehandlingerBegrensetTilBehandlingsstatuser(null, null, Behandlingstype.VEILEDNING, listOf(Behandlingsstatus.UNDER_BEHANDLING, Behandlingsstatus.IKKE_PAABEGYNT), Pageable.unpaged())

        // then
        assertThat(behandlinger.size).isEqualTo(2)
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
        assertThat(behandlinger.size).isEqualTo(2)
    }
}