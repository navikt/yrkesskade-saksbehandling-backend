package no.nav.yrkesskade.saksbehandling.graphql

import com.graphql.spring.boot.test.GraphQLTest
import com.graphql.spring.boot.test.GraphQLTestTemplate
import no.nav.yrkesskade.saksbehandling.config.GraphQLScalarsConfig
import no.nav.yrkesskade.saksbehandling.fixtures.genererBehandling
import no.nav.yrkesskade.saksbehandling.fixtures.genererSak
import no.nav.yrkesskade.saksbehandling.fixtures.okRespons
import no.nav.yrkesskade.saksbehandling.graphql.client.saf.SafClient
import no.nav.yrkesskade.saksbehandling.model.BehandlingEntity
import no.nav.yrkesskade.saksbehandling.model.BehandlingEntityFactory
import no.nav.yrkesskade.saksbehandling.model.BehandlingEntityFactory.Companion.medBehandlingstype
import no.nav.yrkesskade.saksbehandling.model.BehandlingEntityFactory.Companion.medStatus
import no.nav.yrkesskade.saksbehandling.model.Behandlingsstatus
import no.nav.yrkesskade.saksbehandling.model.Behandlingstype
import no.nav.yrkesskade.saksbehandling.repository.BehandlingRepository
import no.nav.yrkesskade.saksbehandling.security.AutentisertBruker
import no.nav.yrkesskade.saksbehandling.test.AbstractTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.kotlin.any
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
    lateinit var safClient: SafClient

    @Autowired
    lateinit var autentisertBruker: AutentisertBruker

    private val aapenBehandling: BehandlingEntity = genererBehandling(1L, null, Behandlingsstatus.IKKE_PAABEGYNT, genererSak())
    private val paabegyntBehandling: BehandlingEntity = genererBehandling(1L, "test", Behandlingsstatus.UNDER_BEHANDLING, genererSak())

    @BeforeEach
    fun startup() {

    }

    @Test
    fun `overta behandling - behandling eksisterer`() {
        Mockito.`when`(behandlingRepository.findById(any())).thenReturn(Optional.of(aapenBehandling))
        Mockito.`when`(behandlingRepository.save(any())).thenAnswer{
            it.arguments.first()
        }
        val response = graphQLTestTemplate.postForResource("graphql/behandling/overta_behandling.graphql")
        assertThat(response.statusCode.is2xxSuccessful).isTrue
        assertThat(response.get("$.data.overtaBehandling.status")).isEqualTo(Behandlingsstatus.UNDER_BEHANDLING.kode)
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
        assertThat(response.get("$.data.ferdigstillBehandling.status")).isEqualTo(Behandlingsstatus.FERDIG.kode)
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
    fun `ferdigstill journalfoering`() {
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")
        Mockito.`when`(behandlingRepository.findById(any())).thenReturn(Optional.of(paabegyntBehandling))
        Mockito.`when`(behandlingRepository.save(any())).thenAnswer{
            it.arguments.first()
        }
        val response = graphQLTestTemplate.postForResource("graphql/behandling/ferdigstill_journalfoering_behandling.graphql")
        assertThat(response.statusCode.is2xxSuccessful).isTrue
        assertThat(response.get("$.data.ferdigstillBehandling.status")).isEqualTo(Behandlingsstatus.FERDIG.kode)
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
        assertThat(response.get("$.data.leggTilbakeBehandling.status")).isEqualTo(Behandlingsstatus.UNDER_BEHANDLING.kode)
    }

    @Test
    fun `overfoer behandling til legacy`() {
        // given
        val behandling = BehandlingEntityFactory.enBehandling(saksbehandlingsansvarligIdent = "test").medStatus(Behandlingsstatus.UNDER_BEHANDLING).medBehandlingstype(Behandlingstype.JOURNALFOERING)
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")
        Mockito.`when`(behandlingRepository.findById(any())).thenReturn(Optional.of(behandling))
        Mockito.`when`(safClient.hentOppdatertJournalpost(ArgumentMatchers.anyString())).thenReturn(okRespons().data)


        // when
        val response = graphQLTestTemplate.postForResource("graphql/behandling/overfoer_behandling.graphql")

        // then
        assertThat(response.statusCode.is2xxSuccessful).isTrue
        assertThat(response.get("$.data.overforBehandlingTilLegacy")).isEqualTo("true")
    }
}