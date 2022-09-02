package no.nav.yrkesskade.saksbehandling.service

import com.expediagroup.graphql.generated.Journalpost
import com.expediagroup.graphql.generated.enums.BrukerIdType
import io.mockk.coEvery
import no.nav.yrkesskade.saksbehandling.fixtures.*
import no.nav.yrkesskade.saksbehandling.graphql.client.SafClient
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
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.Pageable
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit

@Suppress("NonAsciiCharacters")
class BehandlingServiceTest : AbstractTest() {

    @MockBean
    lateinit var autentisertBruker: AutentisertBruker

    @MockBean
    lateinit var safClient: SafClient

    @MockBean
    lateinit var kodeverkService: KodeverkService

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
    fun `hent behandling`() {
        Mockito.`when`(safClient.hentOppdatertJournalpost(anyString())).thenReturn(okRespons().data)
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")
        val behandling = behandlingRepository.save(genererBehandling(1L, "test", Behandlingsstatus.IKKE_PAABEGYNT, sak))

        val detaljertBehandling = behandlingService.hentBehandling(behandling.behandlingId)
        assertThat(detaljertBehandling.dokumenter.size).isEqualTo(1)
    }

    @Test
    fun `hent behandling som ikke har journalpost med dokumenter`() {
        Mockito.`when`(safClient.hentOppdatertJournalpost(anyString())).thenReturn(okResponsUtenDokumenter().data)
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")
        val behandling = behandlingRepository.save(genererBehandling(1L, "test", Behandlingsstatus.IKKE_PAABEGYNT, sak))

        val detaljertBehandling = behandlingService.hentBehandling(behandling.behandlingId)
        assertThat(detaljertBehandling.dokumenter.size).isEqualTo(0)
    }

    @Test
    fun `hent behandling som ikke har en oppdatert journalpost`() {
        Mockito.`when`(safClient.hentOppdatertJournalpost(anyString())).thenReturn(errorRespons().data)
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")
        val behandling = behandlingRepository.save(genererBehandling(1L, "test", Behandlingsstatus.IKKE_PAABEGYNT, sak))

        val detaljertBehandling = behandlingService.hentBehandling(behandling.behandlingId)
        assertThat(detaljertBehandling.dokumenter.size).isEqualTo(0)
    }

    @Test
    fun `hent behandling som ikke finnes`() {
        Mockito.`when`(safClient.hentOppdatertJournalpost(anyString())).thenReturn(okRespons().data)
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")

        assertThrows<NoSuchElementException> {
            behandlingService.hentBehandling(1)
        }
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
    fun `overta behandling som tilhører en annen saksbehandler`() {
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("todd")
        var behandling = genererBehandling(1L, "test", Behandlingsstatus.UNDER_BEHANDLING, sak)
        behandling = behandlingRepository.save(behandling)
        assertThat(behandling.status).isEqualTo(Behandlingsstatus.UNDER_BEHANDLING)
        assertThat(behandlingService.hentAntallBehandlinger()).isEqualTo(1)

        assertThrows<BehandlingException> {
            behandlingService.overtaBehandling(behandling.behandlingId)
        }
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

    @Test
    fun `ferdigstill behandling`() {
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")
        var behandling = genererBehandling(1L, "test", Behandlingsstatus.UNDER_BEHANDLING, sak)
        behandling = behandlingRepository.save(behandling)
        assertThat(behandling.status).isEqualTo(Behandlingsstatus.UNDER_BEHANDLING)
        assertThat(behandlingService.hentAntallBehandlinger()).isEqualTo(1)

        val lagretBehandling = behandlingService.ferdigstillBehandling(behandling.behandlingId)
        assertThat(lagretBehandling.saksbehandlingsansvarligIdent).isEqualTo("test")
        assertThat(lagretBehandling.status).isEqualTo(Behandlingsstatus.FERDIG)
        assertThat(behandlingService.hentAntallBehandlinger()).isEqualTo(1)
    }

    @Test
    fun `ferdigstill behandling som tilhører en annen saksbehandler`() {
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("todd")
        var behandling = genererBehandling(1L, "test", Behandlingsstatus.UNDER_BEHANDLING, sak)
        behandling = behandlingRepository.save(behandling)
        assertThat(behandling.status).isEqualTo(Behandlingsstatus.UNDER_BEHANDLING)
        assertThat(behandlingService.hentAntallBehandlinger()).isEqualTo(1)

        assertThrows<BehandlingException> {
            behandlingService.ferdigstillBehandling(behandling.behandlingId)
        }
    }

    @Test
    fun `ferdigstill behandling som som ikke er under behandling`() {
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")
        var behandling = genererBehandling(1L, "test", Behandlingsstatus.IKKE_PAABEGYNT, sak)
        behandling = behandlingRepository.save(behandling)
        assertThat(behandling.status).isEqualTo(Behandlingsstatus.IKKE_PAABEGYNT)
        assertThat(behandlingService.hentAntallBehandlinger()).isEqualTo(1)

        assertThrows<BehandlingException> {
            behandlingService.ferdigstillBehandling(behandling.behandlingId)
        }
    }

    @Test
    fun `hent behandling dtos`() {
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")
        Mockito.`when`(kodeverkService.hentKodeverk(eq("behandlingstype"), eq(null), any())).thenReturn(behandlingstyper())
        Mockito.`when`(kodeverkService.hentKodeverk(eq("behandlingsstatus"), eq(null), any())).thenReturn(behandlingsstatus())
        Mockito.`when`(kodeverkService.hentKodeverk(eq("framdriftsstatus"), eq(null), any())).thenReturn(framdriftsstatus())

        behandlingRepository.save(genererBehandling(1L, "test", Behandlingsstatus.IKKE_PAABEGYNT, sak))

        val behandlingDtos = behandlingService.hentBehandlingDtos(Pageable.unpaged())
        assertThat(behandlingDtos.size).isEqualTo(1)

        val dto = behandlingDtos.first()
        assertThat(dto.behandlingId).isNotNull
        assertThat(dto.tema).isEqualTo("YRK")
        assertThat(dto.brukerId).isEqualTo("12345")
        assertThat(dto.brukerIdType).isEqualTo(BrukerIdType.AKTOERID)
        assertThat(dto.behandlendeEnhet).isEqualTo("9999")
        assertThat(dto.saksbehandlingsansvarligIdent).isEqualTo("test")
        assertThat(dto.behandlingstype).isEqualTo("Veiledning")
        assertThat(dto.status).isEqualTo("Ikke påbegynt")
        assertThat(dto.behandlingsfrist.truncatedTo(ChronoUnit.DAYS))
            .isEqualTo(Instant.now().plus(30, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS))
        assertThat(dto.journalpostId).isEqualTo("213123123")
        assertThat(dto.dokumentkategori).isEqualTo("enFinKategori")
        assertThat(dto.systemreferanse).isEqualTo("referanse")
        assertThat(dto.framdriftsstatus).isEqualTo("Ikke påbegynt")
        assertThat(dto.opprettetTidspunkt.truncatedTo(ChronoUnit.DAYS))
            .isEqualTo(Instant.now().truncatedTo(ChronoUnit.DAYS))
        assertThat(dto.opprettetAv).isEqualTo("test")
        assertThat(dto.endretAv).isNull()
        assertThat(dto.sak).isEqualTo(sak)
        assertThat(dto.behandlingResultater).isEmpty()
    }

}