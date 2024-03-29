package no.nav.yrkesskade.saksbehandling.service

import com.expediagroup.graphql.generated.enums.BrukerIdType
import com.expediagroup.graphql.generated.enums.Journalposttype
import no.nav.yrkesskade.saksbehandling.client.dokarkiv.DokarkivClient
import no.nav.yrkesskade.saksbehandling.client.oppgave.OppgaveClient
import no.nav.yrkesskade.saksbehandling.client.oppgave.OppgaveFactory
import no.nav.yrkesskade.saksbehandling.fixtures.*
import no.nav.yrkesskade.saksbehandling.graphql.client.saf.JournalpostFactory
import no.nav.yrkesskade.saksbehandling.graphql.client.saf.JournalpostFactory.Companion.medDokumenter
import no.nav.yrkesskade.saksbehandling.graphql.client.saf.JournalpostFactory.Companion.medJournalpostId
import no.nav.yrkesskade.saksbehandling.graphql.client.saf.JournalpostFactory.Companion.medJournalposttype
import no.nav.yrkesskade.saksbehandling.graphql.client.saf.SafClient
import no.nav.yrkesskade.saksbehandling.graphql.common.model.*
import no.nav.yrkesskade.saksbehandling.model.*
import no.nav.yrkesskade.saksbehandling.model.BehandlingEntityFactory.Companion.medBehandlingstype
import no.nav.yrkesskade.saksbehandling.model.BehandlingEntityFactory.Companion.medJournalpostId
import no.nav.yrkesskade.saksbehandling.model.BehandlingEntityFactory.Companion.medSak
import no.nav.yrkesskade.saksbehandling.model.BehandlingEntityFactory.Companion.medStatus
import no.nav.yrkesskade.saksbehandling.model.BehandlingEntityFactory.Companion.medUtgaaendeJournalpostId
import no.nav.yrkesskade.saksbehandling.repository.BehandlingRepository
import no.nav.yrkesskade.saksbehandling.repository.BehandlingsoverfoeringLogRepository
import no.nav.yrkesskade.saksbehandling.repository.SakRepository
import no.nav.yrkesskade.saksbehandling.security.AutentisertBruker
import no.nav.yrkesskade.saksbehandling.test.AbstractTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.NoSuchElementException

@Suppress("NonAsciiCharacters")
class BehandlingServiceTest : AbstractTest() {

    @MockBean
    lateinit var autentisertBruker: AutentisertBruker

    @MockBean
    lateinit var dokarkivClient: DokarkivClient

    @MockBean
    lateinit var safClient: SafClient

    @MockBean
    lateinit var oppgaveClient: OppgaveClient

    @MockBean
    lateinit var kodeverkService: KodeverkService

    @Autowired
    lateinit var behandlingService: BehandlingService

    @Autowired
    lateinit var behandlingRepository: BehandlingRepository

    @Autowired
    lateinit var behandlingsoverfoeringLogRepository: BehandlingsoverfoeringLogRepository

    @Autowired
    lateinit var sakRepository: SakRepository

    lateinit var sak: SakEntity

    @BeforeEach
    fun setUp() {
        resetDatabase()
        sak = SakEntityFactory.enSak()
        sak = sakRepository.save(sak)
        mockKodeverk()
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

    @Transactional
    fun resetDatabase() {
        behandlingRepository.deleteAll()
        behandlingsoverfoeringLogRepository.deleteAll()
        sakRepository.deleteAll()
    }

    @Test
    fun `lagre behandling`() {
        val behandling = genererBehandling(1L, "test", Behandlingsstatus.IKKE_PAABEGYNT, sak)
        behandlingService.lagreBehandling(behandling)
        val behandlinger = behandlingService.hentBehandlinger(Pageable.unpaged())
        assertThat(behandlinger.size).isEqualTo(1)
    }

    @Test
    fun `hent egne behandlinger`() {
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")
        behandlingRepository.save(genererBehandling(1L, "test", Behandlingsstatus.UNDER_BEHANDLING, sak))
        behandlingRepository.save(genererBehandling(10L, "todd", Behandlingsstatus.UNDER_BEHANDLING, sak))

        val behandlinger = behandlingService.hentBehandlinger(Pageable.unpaged())
        assertThat(behandlinger.size).isEqualTo(2)

        val egneBehandlingerPage = behandlingService.hentEgneBehandlinger(
            page = PageRequest.of(0, 10),
            behandlingsstatus = Behandlingsstatus.UNDER_BEHANDLING.name,
            tidsfilter = null
        )
        assertThat(egneBehandlingerPage.behandlinger.size).isEqualTo(1)
    }

    @Test
    fun `hent egne ferdigstilte behandlinger siste 30 dager`() {
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")
        val nyereSiden = Instant.now().minus(30, ChronoUnit.DAYS)
        val behandlinger = mutableListOf<BehandlingEntity>()
        repeat(50) {
            val behandling = behandlingRepository.save(BehandlingEntityFactory.enBehandling("test").medSak(sak))
            val endretTidspunkt = behandling.endretTidspunkt
            if ((endretTidspunkt != null && endretTidspunkt.isAfter(nyereSiden)) && (behandling.status == Behandlingsstatus.FERDIG)) {
                behandlinger.add(behandling)
            }

        }

        val egneBehandlingerPage = behandlingService.hentEgneBehandlinger(Behandlingsstatus.FERDIG.name, page = PageRequest.of(0, 100), Tidsfilter(endretSiden = nyereSiden, opprettetSiden = null))
        assertThat(egneBehandlingerPage.totaltAntallBehandlinger).isEqualTo(behandlinger.size.toLong())
    }

    @Test
    fun `hent behandling`() {
        Mockito.`when`(safClient.hentOppdatertJournalpost(anyString())).thenReturn(okRespons().data)
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")
        val behandling = behandlingRepository.save(genererBehandling(1L, "test", Behandlingsstatus.IKKE_PAABEGYNT, sak))

        val detaljertBehandling = behandlingService.hentDetaljertBehandling(behandling.behandlingId)
        assertThat(detaljertBehandling.dokumenter.size).isEqualTo(1)
    }

    @Test
    fun `hent behandling med inngående og utgående journalposter`() {
        // given
        val utgaaendeDokument = JournalpostFactory.ettDokument()
        val utgaaendeJournalpost = JournalpostFactory.enJournalpost().medJournalposttype(Journalposttype.U).medDokumenter(listOf(utgaaendeDokument))
        val utgaaendeJournalpostId =utgaaendeJournalpost.journalpostId
        val dokument = JournalpostFactory.ettDokument()
        val journalpost = JournalpostFactory.enJournalpost().medDokumenter(listOf(dokument)).medJournalposttype(Journalposttype.I)
        val journalpostId = journalpost.journalpostId
        val givenBehandling = BehandlingEntityFactory.enBehandling("test").medStatus(Behandlingsstatus.IKKE_PAABEGYNT).medSak(sak).medJournalpostId(journalpostId).medUtgaaendeJournalpostId(utgaaendeJournalpostId)

        Mockito.`when`(safClient.hentOppdatertJournalpost(eq(journalpostId))).thenReturn(okRespons(journalpost).data)
        Mockito.`when`(safClient.hentOppdatertJournalpost(eq(utgaaendeJournalpostId))).thenReturn(okRespons(utgaaendeJournalpost).data)
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")
        val behandling = behandlingRepository.save(givenBehandling)

        // when
        val detaljertBehandling = behandlingService.hentDetaljertBehandling(behandling.behandlingId)

        // then
        assertThat(detaljertBehandling.dokumenter.size).isEqualTo(2)
        assertThat(detaljertBehandling.dokumenter.filter { it.tittel == utgaaendeDokument.tittel }.size).isEqualTo(1)
        assertThat(detaljertBehandling.dokumenter.filter { it.tittel == dokument.tittel }.size).isEqualTo(1)
        assertThat(detaljertBehandling.utgaaendeJournalpostId).isEqualTo(utgaaendeJournalpostId)
    }

    @Test
    fun `hent behandling som ikke har journalpost med dokumenter`() {
        Mockito.`when`(safClient.hentOppdatertJournalpost(anyString())).thenReturn(okResponsUtenDokumenter().data)
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")
        val behandling = behandlingRepository.save(genererBehandling(1L, "test", Behandlingsstatus.IKKE_PAABEGYNT, sak))

        val detaljertBehandling = behandlingService.hentDetaljertBehandling(behandling.behandlingId)
        assertThat(detaljertBehandling.dokumenter.size).isEqualTo(0)
    }

    @Test
    fun `hent behandling som ikke har en oppdatert journalpost`() {
        Mockito.`when`(safClient.hentOppdatertJournalpost(anyString())).thenReturn(errorRespons().data)
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")
        val behandling = behandlingRepository.save(genererBehandling(1L, "test", Behandlingsstatus.IKKE_PAABEGYNT, sak))

        val detaljertBehandling = behandlingService.hentDetaljertBehandling(behandling.behandlingId)
        assertThat(detaljertBehandling.dokumenter.size).isEqualTo(0)
    }

    @Test
    fun `hent behandling som ikke finnes`() {
        Mockito.`when`(safClient.hentOppdatertJournalpost(anyString())).thenReturn(okRespons().data)
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")

        assertThrows<NoSuchElementException> {
            behandlingService.hentDetaljertBehandling(1)
        }
    }

    @Test
    fun `overta behandling`() {
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")
        val behandling = behandlingRepository.save(genererBehandling(1L, "test", Behandlingsstatus.IKKE_PAABEGYNT, sak))
        assertThat(behandling.status).isNotEqualTo(Behandlingsstatus.UNDER_BEHANDLING)
        assertThat(behandlingService.hentAntallBehandlinger()).isEqualTo(1)

        val lagretBehandling = behandlingService.overtaBehandling(behandling.behandlingId)
        assertThat(lagretBehandling.saksbehandlingsansvarligIdent).isEqualTo("test")
        assertThat(lagretBehandling.status).isEqualTo(Behandlingsstatus.UNDER_BEHANDLING.kode)
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
        var behandling = genererBehandling(1L, null, Behandlingsstatus.UNDER_BEHANDLING, sak)
        behandling = behandlingRepository.save(behandling)
        assertThat(behandlingService.hentAntallBehandlinger()).isEqualTo(1)

        assertThrows<IllegalStateException> {
            behandlingService.leggTilbakeBehandling(behandling.behandlingId)
        }
    }

    @Test
    fun `legg tilbake behandling for behandling som har en annen saksbehandler`() {
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("todd")
        var behandling = genererBehandling(1L, "test", Behandlingsstatus.UNDER_BEHANDLING, sak)
        behandling = behandlingRepository.save(behandling)
        assertThat(behandlingService.hentAntallBehandlinger()).isEqualTo(1)

        assertThrows<IllegalStateException> {
            behandlingService.leggTilbakeBehandling(behandling.behandlingId)
        }
    }

    @Test
    fun `legg tilbake egen behandling`() {
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")
        val behandling =
            behandlingRepository.save(genererBehandling(1L, "test", Behandlingsstatus.UNDER_BEHANDLING, sak))
        assertThat(behandlingService.hentAntallBehandlinger()).isEqualTo(1)

        val lagretBehandling = behandlingService.leggTilbakeBehandling(behandling.behandlingId)
        assertThat(lagretBehandling.saksbehandlingsansvarligIdent).isNull()
        assertThat(lagretBehandling.status).isEqualTo(Behandlingsstatus.UNDER_BEHANDLING.kode)
        assertThat(behandlingService.hentAntallBehandlinger()).isEqualTo(1)
    }

    @Test
    fun `ferdigstill journalfoeringsbehandling`() {
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")
        var behandling = BehandlingEntityFactory.enBehandling("test").medBehandlingstype(Behandlingstype.JOURNALFOERING)
            .medStatus(Behandlingsstatus.UNDER_BEHANDLING).medSak(sak)
        behandling = behandlingRepository.save(behandling)
        assertThat(behandling.status).isEqualTo(Behandlingsstatus.UNDER_BEHANDLING)
        assertThat(behandlingService.hentAntallBehandlinger()).isEqualTo(1)

        val lagretBehandling = behandlingService.ferdigstillBehandling(FerdigstillBehandling(behandling.behandlingId))
        assertThat(lagretBehandling.behandling.saksbehandlingsansvarligIdent).isEqualTo("test")
        assertThat(lagretBehandling.behandling.status).isEqualTo(Behandlingsstatus.FERDIG.kode)
        assertThat(lagretBehandling.nesteBehandling?.saksbehandlingsansvarligIdent).isEqualTo("test")
        assertThat(lagretBehandling.nesteBehandling?.status).isEqualTo(Behandlingsstatus.UNDER_BEHANDLING.kode)
        assertThat(behandlingService.hentAntallBehandlinger()).isEqualTo(2)
        Mockito.verify(dokarkivClient).ferdigstillJournalpost(any(), any())
    }

    @Test
    fun `ferdigstill veiledingsbehandling`() {
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")
        var behandling =
            genererBehandling(1L, "test", Behandlingsstatus.UNDER_BEHANDLING, sak, Behandlingstype.VEILEDNING)
        behandling = behandlingRepository.save(behandling)
        assertThat(behandling.status).isEqualTo(Behandlingsstatus.UNDER_BEHANDLING)
        assertThat(behandlingService.hentAntallBehandlinger()).isEqualTo(1)

        val lagretBehandling = behandlingService.ferdigstillBehandling(FerdigstillBehandling(behandling.behandlingId))
        assertThat(lagretBehandling.behandling.saksbehandlingsansvarligIdent).isEqualTo("test")
        assertThat(lagretBehandling.behandling.status).isEqualTo(Behandlingsstatus.FERDIG.kode)
        assertThat(lagretBehandling.nesteBehandling).isNull()
        assertThat(behandlingService.hentAntallBehandlinger()).isEqualTo(1)
        Mockito.verify(dokarkivClient, never()).ferdigstillJournalpost(any(), any())
    }

    @Test
    fun `transactional ruller tilbake DB-endringer`() {
        Mockito.`when`(dokarkivClient.ferdigstillJournalpost(any(), any())).thenThrow(RuntimeException())
        val behandlingFoerRollback = behandlingRepository.save(
            genererBehandling(1L, "test", Behandlingsstatus.UNDER_BEHANDLING, sak, Behandlingstype.JOURNALFOERING)
        )

        assertThat(behandlingFoerRollback.status).isEqualTo(Behandlingsstatus.UNDER_BEHANDLING)

        assertThrows<RuntimeException> {
            behandlingService.ferdigstillBehandling(FerdigstillBehandling(behandlingFoerRollback.behandlingId))
        }
        val behandlingEtterRollback = behandlingService.hentBehandling(behandlingId = behandlingFoerRollback.behandlingId)
        assertThat(behandlingEtterRollback.status).isEqualTo(Behandlingsstatus.UNDER_BEHANDLING)
        assertThat(behandlingService.hentAntallBehandlinger()).isEqualTo(1)
    }

    @Test
    fun `lagre utgående journalpostId etter brevutsending`() {
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")
        val journalfoeringsbehandling = behandlingRepository.save(
            BehandlingEntityFactory.enBehandling("test")
                .medSak(sak)
                .medBehandlingstype(Behandlingstype.JOURNALFOERING)
                .medStatus(Behandlingsstatus.FERDIG)
        )
        assertThat(journalfoeringsbehandling.status).isEqualTo(Behandlingsstatus.FERDIG)
        assertThat(behandlingService.hentAntallBehandlinger()).isEqualTo(1)

        val veiledningsbehandling = behandlingRepository.save(
            BehandlingEntityFactory.enBehandling("test")
                .medSak(sak)
                .medBehandlingstype(Behandlingstype.VEILEDNING)
                .medStatus(Behandlingsstatus.FERDIG)
                .medJournalpostId(journalfoeringsbehandling.journalpostId)
        )
        assertThat(veiledningsbehandling.status).isEqualTo(Behandlingsstatus.FERDIG)
        assertThat(behandlingService.hentAntallBehandlinger()).isEqualTo(2)

        val utgaaendejournalpostId = "1234"
        behandlingService.lagreUtgaaendeJournalpostFraBrevutsending(
            behandlingId = veiledningsbehandling.behandlingId,
            journalpostId = utgaaendejournalpostId
        )

        val oppdatertJournalfoeringsbehandling = behandlingService.hentBehandling(journalfoeringsbehandling.behandlingId)
        val oppdatertVeiledningsbehandling = behandlingService.hentBehandling(veiledningsbehandling.behandlingId)

        assertThat(oppdatertJournalfoeringsbehandling.utgaaendeJournalpostId).isEqualTo(utgaaendejournalpostId)
        assertThat(oppdatertJournalfoeringsbehandling.utgaaendeJournalpostId)
            .isEqualTo(oppdatertVeiledningsbehandling.utgaaendeJournalpostId)

        assertThat(behandlingService.hentAntallBehandlinger()).isEqualTo(2)
    }

    @Test
    fun `ferdigstill behandling som tilhører en annen saksbehandler`() {
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("todd")
        var behandling = genererBehandling(1L, "test", Behandlingsstatus.UNDER_BEHANDLING, sak)
        behandling = behandlingRepository.save(behandling)
        assertThat(behandling.status).isEqualTo(Behandlingsstatus.UNDER_BEHANDLING)
        assertThat(behandlingService.hentAntallBehandlinger()).isEqualTo(1)

        assertThrows<BehandlingException> {
            behandlingService.ferdigstillBehandling(FerdigstillBehandling(behandling.behandlingId))
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
            behandlingService.ferdigstillBehandling(FerdigstillBehandling(behandling.behandlingId))
        }
    }

    @Test
    fun `hent behandling dtos`() {
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")

        behandlingRepository.save(genererBehandling(1L, "test", Behandlingsstatus.IKKE_PAABEGYNT, sak))

        val behandlingDtos = behandlingService.hentBehandlinger(Pageable.unpaged())
        assertThat(behandlingDtos.size).isEqualTo(1)

        val dto = behandlingDtos.first()
        assertThat(dto.behandlingId).isNotNull
        assertThat(dto.tema).isEqualTo("YRK")
        assertThat(dto.brukerId).isEqualTo("01010112345")
        assertThat(dto.brukerIdType).isEqualTo(BrukerIdType.FNR)
        assertThat(dto.behandlendeEnhet).isEqualTo("9999")
        assertThat(dto.saksbehandlingsansvarligIdent).isEqualTo("test")
        assertThat(dto.behandlingstype).isEqualTo(Behandlingstype.VEILEDNING.kode)
        assertThat(dto.status).isEqualTo(Behandlingsstatus.IKKE_PAABEGYNT.kode)
        assertThat(dto.behandlingsfrist.truncatedTo(ChronoUnit.DAYS))
            .isEqualTo(Instant.now().plus(30, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS))
        assertThat(dto.journalpostId).isEqualTo("213123123")
        assertThat(dto.dokumentkategori).isEqualTo("enFinKategori")
        assertThat(dto.systemreferanse).isEqualTo("referanse")
        assertThat(dto.framdriftsstatus).isEqualTo(Framdriftsstatus.IKKE_PAABEGYNT.kode)
        assertThat(dto.opprettetTidspunkt.truncatedTo(ChronoUnit.DAYS))
            .isEqualTo(Instant.now().truncatedTo(ChronoUnit.DAYS))
        assertThat(dto.opprettetAv).isEqualTo("test")
        assertThat(dto.endretAv).isNull()
        assertThat(dto.sak).isEqualTo(sak)
        assertThat(dto.behandlingResultater).isEmpty()
    }

    @Test
    fun `hent aapne behandlinger med behandlingstype journalfoering`() {
        // given
        behandlingRepository.save(genererBehandling(10L, null, Behandlingsstatus.IKKE_PAABEGYNT, sak))
        behandlingRepository.save(
            genererBehandling(
                11L,
                null,
                Behandlingsstatus.IKKE_PAABEGYNT,
                sak,
                Behandlingstype.JOURNALFOERING
            )
        )
        behandlingRepository.save(genererBehandling(12L, null, Behandlingsstatus.UNDER_BEHANDLING, sak))
        behandlingRepository.save(
            genererBehandling(
                13L,
                null,
                Behandlingsstatus.UNDER_BEHANDLING,
                sak,
                Behandlingstype.JOURNALFOERING
            )
        )

        // when
        val behandlingsPage = behandlingService.hentAapneBehandlinger(
            behandlingsfilter = Behandlingsfilter(
                behandlingstype = Behandlingstype.JOURNALFOERING.kode,
                null,
                null,
            ), page = PageRequest.of(0, 10)
        )

        // then
        assertThat(behandlingsPage.behandlinger.size).isEqualTo(2)
        assertThat(behandlingsPage.behandlinger.first().behandlingstype).isEqualTo(Behandlingstype.JOURNALFOERING.kode)
    }

    @Test
    fun `hent aapne behandlinger med status underBehandling`() {
        // given
        behandlingRepository.save(genererBehandling(14L, null, Behandlingsstatus.IKKE_PAABEGYNT, sak))
        behandlingRepository.save(
            genererBehandling(
                15L,
                null,
                Behandlingsstatus.IKKE_PAABEGYNT,
                sak,
                Behandlingstype.JOURNALFOERING
            )
        )
        behandlingRepository.save(genererBehandling(16L, null, Behandlingsstatus.UNDER_BEHANDLING, sak))
        behandlingRepository.save(
            genererBehandling(
                17L,
                null,
                Behandlingsstatus.UNDER_BEHANDLING,
                sak,
                Behandlingstype.JOURNALFOERING
            )
        )

        // when
        val behandlingsPage = behandlingService.hentAapneBehandlinger(
            behandlingsfilter = Behandlingsfilter(
                behandlingstype = null,
                null,
                status = Behandlingsstatus.UNDER_BEHANDLING.kode
            ), page = PageRequest.of(0, 10)
        )

        // then
        assertThat(behandlingsPage.behandlinger.size).isEqualTo(2)
        assertThat(behandlingsPage.behandlinger.first().status).isEqualTo(Behandlingsstatus.UNDER_BEHANDLING.kode)
    }

    @Test
    fun `hent aapne behandlinger med dokumentkategori tannlegeerklaering`() {
        // given
        behandlingRepository.save(genererBehandling(18L, null, Behandlingsstatus.IKKE_PAABEGYNT, sak))
        behandlingRepository.save(
            genererBehandling(
                19L,
                null,
                Behandlingsstatus.IKKE_PAABEGYNT,
                sak,
                Behandlingstype.JOURNALFOERING
            )
        )
        behandlingRepository.save(genererBehandling(20L, null, Behandlingsstatus.UNDER_BEHANDLING, sak))
        behandlingRepository.save(
            genererBehandling(
                21L,
                null,
                Behandlingsstatus.UNDER_BEHANDLING,
                sak,
                Behandlingstype.JOURNALFOERING
            )
        )

        // when
        val behandlingsPage = behandlingService.hentAapneBehandlinger(
            behandlingsfilter = Behandlingsfilter(
                behandlingstype = null,
                dokumentkategori = "enFinKategori",
                status = null
            ), page = PageRequest.of(0, 10)
        )

        // then
        assertThat(behandlingsPage.behandlinger.size).isEqualTo(4)
        assertThat(behandlingsPage.behandlinger.first().dokumentkategori).isEqualTo("enFinKategori")
    }

    @Test
    fun `overfoer behandling til legacy system (opprett journalpostoppgave)`() {
        // given
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")
        Mockito.`when`(safClient.hentOppdatertJournalpost(anyString())).thenReturn(okRespons().data)
        Mockito.`when`(oppgaveClient.opprettOppgave(any())).thenReturn(OppgaveFactory.enOppgave())
        val behandling = behandlingRepository.save(
            BehandlingEntityFactory.enBehandling("test")
                .medSak(sak)
                .medBehandlingstype(Behandlingstype.JOURNALFOERING)
                .medStatus(Behandlingsstatus.UNDER_BEHANDLING)
        )

        // when
        behandlingService.overforBehandlingTilLegacy(behandling.behandlingId, "Ikke en tannlegeerkæring")

        // then
        val resultater = behandlingsoverfoeringLogRepository.count()
        assertThat(resultater).isEqualTo(1)

        val ikkeEksisterendeBehandling = behandlingRepository.findById(behandling.behandlingId)
        assertThat(ikkeEksisterendeBehandling.isEmpty).isTrue()
    }

    @Test
    fun `overfoer behandling til legacy system (opprett journalpostoppgave) - feil saksbehandlinger`() {
        // given
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("ingen")
        Mockito.`when`(safClient.hentOppdatertJournalpost(anyString())).thenReturn(okRespons().data)
        val behandling = behandlingRepository.save(
            BehandlingEntityFactory.enBehandling("test")
                .medSak(sak)
                .medBehandlingstype(Behandlingstype.JOURNALFOERING)
                .medStatus(Behandlingsstatus.UNDER_BEHANDLING)
        )

        // when
        assertThrows<IllegalStateException> {
            behandlingService.overforBehandlingTilLegacy(behandling.behandlingId, "ingen")
        }
    }

    @Test
    fun `overfoer behandling til legacy system (opprett journalpostoppgave) - ingen journalføring funnet`() {
        // given
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")
        val behandling = behandlingRepository.save(
            BehandlingEntityFactory.enBehandling("test")
                .medSak(sak)
                .medBehandlingstype(Behandlingstype.JOURNALFOERING)
                .medStatus(Behandlingsstatus.UNDER_BEHANDLING)
        )

        // when
        assertThrows<IllegalStateException> {
            behandlingService.overforBehandlingTilLegacy(behandling.behandlingId, "ingen")
        }
    }

    @Test
    fun `overfoer behandling til legacy system (opprett journalpostoppgave) - er ikke journalfoering`() {
        // given
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")
        Mockito.`when`(safClient.hentOppdatertJournalpost(anyString())).thenReturn(okRespons().data)
        val behandling = behandlingRepository.save(
            BehandlingEntityFactory.enBehandling("test")
                .medSak(sak)
                .medBehandlingstype(Behandlingstype.VEILEDNING)
                .medStatus(Behandlingsstatus.UNDER_BEHANDLING)
        )

        // when
        assertThrows<IllegalStateException> {
            behandlingService.overforBehandlingTilLegacy(behandling.behandlingId, "ingen")
        }
    }

    @Test
    fun `overfoer behandling til legacy system (opprett journalpostoppgave) - er ferdig`() {
        // given
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")
        Mockito.`when`(safClient.hentOppdatertJournalpost(anyString())).thenReturn(okRespons().data)
        val behandling = behandlingRepository.save(
            BehandlingEntityFactory.enBehandling("test")
                .medSak(sak)
                .medBehandlingstype(Behandlingstype.JOURNALFOERING)
                .medStatus(Behandlingsstatus.FERDIG)
        )

        // when
        assertThrows<IllegalStateException> {
            behandlingService.overforBehandlingTilLegacy(behandling.behandlingId, "ingen")
        }
    }
}