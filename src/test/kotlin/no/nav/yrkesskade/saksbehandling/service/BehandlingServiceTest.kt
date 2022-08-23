package no.nav.yrkesskade.saksbehandling.service

import no.nav.yrkesskade.saksbehandling.fixtures.genererBehandling
import no.nav.yrkesskade.saksbehandling.fixtures.genererSak
import no.nav.yrkesskade.saksbehandling.model.Behandlingsstatus
import no.nav.yrkesskade.saksbehandling.model.SakEntity
import no.nav.yrkesskade.saksbehandling.repository.BehandlingRepository
import no.nav.yrkesskade.saksbehandling.repository.SakRepository
import no.nav.yrkesskade.saksbehandling.security.AutentisertBruker
import no.nav.yrkesskade.saksbehandling.test.AbstractTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.Pageable
import org.springframework.transaction.annotation.Transactional

@Transactional
class BehandlingServiceTest : AbstractTest() {

    @MockBean
    lateinit var autentisertBruker: AutentisertBruker

    @Autowired
    lateinit var behandlingService: BehandlingService

    @Autowired
    lateinit var behandlingRepository: BehandlingRepository

    @Autowired
    lateinit var sakRepository: SakRepository

    lateinit var sak: SakEntity

    @BeforeEach
    fun setUp() {
        resetDatabase()
        sak = genererSak()
        sak = sakRepository.save(sak)
    }

    @Transactional
    fun resetDatabase() {
        behandlingRepository.deleteAll()
        sakRepository.deleteAll()
    }

    @Test
    fun `lagre behandling`() {
        var behandling = genererBehandling(1L, "test", Behandlingsstatus.IKKE_PAABEGYNT, sak)
        behandlingService.lagreBehandling(behandling)
        val behandlinger = behandlingService.hentBehandlinger(Pageable.unpaged())
        assertThat(behandlinger.size).isEqualTo(1)
    }

    @Test
    fun `hent egne behandlinger`() {
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")
        behandlingRepository.save(genererBehandling(1L, "test", Behandlingsstatus.IKKE_PAABEGYNT, sak))
        behandlingRepository.save(genererBehandling(1L, "todd", Behandlingsstatus.IKKE_PAABEGYNT, sak))

        val behandlinger = behandlingService.hentBehandlinger(Pageable.unpaged())
        assertThat(behandlinger.size).isEqualTo(2)

        val egneBehandlinger = behandlingService.hentEgneBehandlinger(Pageable.ofSize(10))
        assertThat(egneBehandlinger.size).isEqualTo(1)
    }

    @Test
    fun `overta behandling`() {
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")
        var behandling = genererBehandling(1L, "test", Behandlingsstatus.IKKE_PAABEGYNT, sak)
        behandling = behandlingRepository.save(behandling)
        assertThat(behandling.status).isNotEqualTo(Behandlingsstatus.UNDER_BEHANDLING)
        assertThat(behandlingService.hentAntallBehandlinger()).isEqualTo(1)

        val lagretBehandling = behandlingService.overtaBehandling(behandling.behandlingId)
        assertThat(lagretBehandling.saksbehandlingsansvarligIdent).isEqualTo("test")
        assertThat(lagretBehandling.status).isEqualTo(Behandlingsstatus.UNDER_BEHANDLING)
        assertThat(behandlingService.hentAntallBehandlinger()).isEqualTo(1)
    }

    @Test
    fun `legg tilbake behandling for behandling som ikke har en saksbehandler`() {
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")
        var behandling = genererBehandling(1L, null, Behandlingsstatus.IKKE_PAABEGYNT, sak)
        behandling = behandlingRepository.save(behandling)
        assertThat(behandlingService.hentAntallBehandlinger()).isEqualTo(1)

        assertThrows<IllegalStateException> {
            behandlingService.leggTilbakeBehandling(behandling.behandlingId)
        }
    }

    @Test
    fun `legg tilbake behandling for behandling som har en annen saksbehandler`() {
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("todd")
        var behandling = genererBehandling(1L, "test", Behandlingsstatus.IKKE_PAABEGYNT, sak)
        behandling = behandlingRepository.save(behandling)
        assertThat(behandlingService.hentAntallBehandlinger()).isEqualTo(1)

        assertThrows<IllegalStateException> {
            behandlingService.leggTilbakeBehandling(behandling.behandlingId)
        }
    }

    @Test
    fun `legg tilbake egen behandling`() {
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")
        var behandling = genererBehandling(1L, "test", Behandlingsstatus.IKKE_PAABEGYNT, sak)
        behandling = behandlingRepository.save(behandling)
        assertThat(behandlingService.hentAntallBehandlinger()).isEqualTo(1)

        val lagretBehandling = behandlingService.leggTilbakeBehandling(behandling.behandlingId)
        assertThat(lagretBehandling.saksbehandlingsansvarligIdent).isNull()
        assertThat(lagretBehandling.status).isEqualTo(Behandlingsstatus.IKKE_PAABEGYNT)
        assertThat(behandlingService.hentAntallBehandlinger()).isEqualTo(1)
    }
}