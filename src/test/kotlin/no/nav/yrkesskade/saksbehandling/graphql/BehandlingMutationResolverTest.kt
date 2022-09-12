package no.nav.yrkesskade.saksbehandling.graphql

import com.graphql.spring.boot.test.GraphQLTest
import com.graphql.spring.boot.test.GraphQLTestTemplate
import no.nav.yrkesskade.saksbehandling.config.GraphQLScalarsConfig
import no.nav.yrkesskade.saksbehandling.fixtures.*
import no.nav.yrkesskade.saksbehandling.model.*
import no.nav.yrkesskade.saksbehandling.repository.BehandlingRepository
import no.nav.yrkesskade.saksbehandling.security.AutentisertBruker
import no.nav.yrkesskade.saksbehandling.service.KodeverkService
import no.nav.yrkesskade.saksbehandling.test.AbstractTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import java.util.*

@Import(value = [GraphQLScalarsConfig::class, GraphQLConfig::class])
@GraphQLTest
class BehandlingMutationResolverTest : AbstractTest() {

    @Autowired
    lateinit var graphQLTestTemplate: GraphQLTestTemplate

    @Autowired
    lateinit var behandlingRepository: BehandlingRepository

    @Autowired
    lateinit var autentisertBruker: AutentisertBruker

    @Autowired
    lateinit var kodeverkService: KodeverkService

    private val aapenBehandling: BehandlingEntity = genererBehandling(1L, null, Behandlingsstatus.IKKE_PAABEGYNT, genererSak())
    private val paabegyntBehandling: BehandlingEntity = genererBehandling(1L, "test", Behandlingsstatus.UNDER_BEHANDLING, genererSak())

    @BeforeEach
    fun startup() {
        Mockito.`when`(kodeverkService.hentKodeverk(eq("behandlingstype"), eq(null), any())).thenReturn(behandlingstyper())
        Mockito.`when`(kodeverkService.hentKodeverk(eq("behandlingsstatus"), eq(null), any())).thenReturn(
            behandlingsstatus()
        )
        Mockito.`when`(kodeverkService.hentKodeverk(eq("framdriftsstatus"), eq(null), any())).thenReturn(
            framdriftsstatus()
        )
    }

    @Test
    fun `overta behandling - behandling eksisterer`() {
        Mockito.`when`(behandlingRepository.findById(any())).thenReturn(Optional.of(aapenBehandling))
        Mockito.`when`(behandlingRepository.save(any())).thenAnswer{
            it.arguments.first()
        }
        val response = graphQLTestTemplate.postForResource("graphql/behandling/overta_behandling.graphql")
        assertThat(response.statusCode.is2xxSuccessful).isTrue
        assertThat(response.get("$.data.overtaBehandling.status")).isEqualTo("Under behandling")
    }

    @Test
    fun `overta behandling - behandling tilhører annend behandler`() {
        Mockito.`when`(behandlingRepository.findById(any())).thenReturn(Optional.of(paabegyntBehandling))
        Mockito.`when`(behandlingRepository.save(any())).thenAnswer{
            it.arguments.first()
        }
        val response = graphQLTestTemplate.postForResource("graphql/behandling/overta_behandling.graphql")
        assertThat(response.statusCode.is2xxSuccessful).isTrue
        assertThat(response.get("$.errors[0].message")).contains("Behandling tilhører en annen saksbehandler")
    }

    @Test
    fun `overta behandling - behandling eksisterer ikke`() {
        Mockito.`when`(behandlingRepository.findById(any())).thenReturn(null)

        val response = graphQLTestTemplate.postForResource("graphql/behandling/overta_behandling.graphql")
        assertThat(response.statusCode.is2xxSuccessful).isTrue
        assertThat(response.get("$.errors.length()")).isEqualTo("1")
        assertThat(response.get("$.data.overtaBehandling")).isNull()
    }

    @Test
    fun `ferdigstill behandling - behandling eksisterer`() {
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")
        Mockito.`when`(behandlingRepository.findById(any())).thenReturn(Optional.of(paabegyntBehandling))
        Mockito.`when`(behandlingRepository.save(any())).thenAnswer{
            it.arguments.first()
        }
        val response = graphQLTestTemplate.postForResource("graphql/behandling/ferdigstill_behandling.graphql")
        assertThat(response.statusCode.is2xxSuccessful).isTrue
        assertThat(response.get("$.data.ferdigstillBehandling.status")).isEqualTo("Ferdig")
    }

    @Test
    fun `ferdigstill behandling - behandling eksisterer ikke`() {
        Mockito.`when`(behandlingRepository.findById(any())).thenReturn(null)

        val response = graphQLTestTemplate.postForResource("graphql/behandling/ferdigstill_behandling.graphql")
        assertThat(response.statusCode.is2xxSuccessful).isTrue
        assertThat(response.get("$.errors.length()")).isEqualTo("1")
        assertThat(response.get("$.data.ferdigstillBehandling")).isNull()
    }

    @Test
    fun `ferdigstill behandling - behandling tilhører annend behandler`() {
        Mockito.`when`(behandlingRepository.findById(any())).thenReturn(Optional.of(paabegyntBehandling))
        Mockito.`when`(behandlingRepository.save(any())).thenAnswer{
            it.arguments.first()
        }
        val response = graphQLTestTemplate.postForResource("graphql/behandling/ferdigstill_behandling.graphql")
        assertThat(response.statusCode.is2xxSuccessful).isTrue
        assertThat(response.get("$.errors[0].message")).contains("Behandling tilhører en annen saksbehandler")
    }

    @Test
    fun `ferdigstill behandling - behandling har feil status`() {
        Mockito.`when`(behandlingRepository.findById(any())).thenReturn(Optional.of(aapenBehandling))
        Mockito.`when`(behandlingRepository.save(any())).thenAnswer{
            it.arguments.first()
        }
        val response = graphQLTestTemplate.postForResource("graphql/behandling/ferdigstill_behandling.graphql")
        assertThat(response.statusCode.is2xxSuccessful).isTrue
        assertThat(response.get("$.errors[0].message")).contains("Kan ikke ferdigstille behandling. Behandling har status ${Behandlingsstatus.IKKE_PAABEGYNT.name}")
    }

    @Test
    fun `legg tilbake behandling`() {
        val behandling = genererBehandling(1, "test", Behandlingsstatus.UNDER_BEHANDLING, genererSak())
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")
        Mockito.`when`(behandlingRepository.findById(any())).thenReturn(Optional.of(behandling))
        Mockito.`when`(behandlingRepository.save(any())).thenAnswer{
            it.arguments.first()
        }

        val response = graphQLTestTemplate.postForResource("graphql/behandling/legg_tilbake_behandling.graphql")
        assertThat(response.statusCode.is2xxSuccessful).isTrue
        assertThat(response.get("$.data.leggTilbakeBehandling.status")).isEqualTo("Ikke påbegynt")
    }
}