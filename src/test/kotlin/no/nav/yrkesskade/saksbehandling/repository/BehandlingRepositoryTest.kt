package no.nav.yrkesskade.saksbehandling.repository

import no.nav.yrkesskade.saksbehandling.fixtures.genererBehandling
import no.nav.yrkesskade.saksbehandling.fixtures.genererSak
import no.nav.yrkesskade.saksbehandling.model.Behandlingsstatus
import no.nav.yrkesskade.saksbehandling.test.AbstractTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.data.domain.Pageable
import org.springframework.test.context.ActiveProfiles
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
        val behandling = genererBehandling(1L, "test", Behandlingsstatus.IKKE_PAABEGYNT, sak)

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
}