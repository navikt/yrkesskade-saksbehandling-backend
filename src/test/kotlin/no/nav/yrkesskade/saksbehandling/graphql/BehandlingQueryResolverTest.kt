package no.nav.yrkesskade.saksbehandling.graphql

import com.graphql.spring.boot.test.GraphQLTest
import com.graphql.spring.boot.test.GraphQLTestTemplate
import no.nav.yrkesskade.saksbehandling.config.GraphQLScalarsConfig
import no.nav.yrkesskade.saksbehandling.fixtures.genererBehandling
import no.nav.yrkesskade.saksbehandling.fixtures.genererSak
import no.nav.yrkesskade.saksbehandling.model.Behandlingsstatus
import no.nav.yrkesskade.saksbehandling.repository.BehandlingRepository
import no.nav.yrkesskade.saksbehandling.security.AutentisertBruker
import no.nav.yrkesskade.saksbehandling.service.BehandlingService
import no.nav.yrkesskade.saksbehandling.test.AbstractTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import java.util.*

@Import(value = [GraphQLScalarsConfig::class, GraphQLConfig::class])
@GraphQLTest
class BehandlingQueryResolverTest : AbstractTest() {

    @Autowired
    lateinit var graphQLTestTemplate: GraphQLTestTemplate

    @Autowired
    lateinit var behandlingService: BehandlingService

    @Autowired
    lateinit var behandlingRepository: BehandlingRepository

    @Autowired
    lateinit var autentisertBruker: AutentisertBruker

    @BeforeEach
    fun startup() {

    }

    @Test
    fun `hent antall behandlinger`() {
        Mockito.`when`(behandlingRepository.count()).thenReturn(1)

        val response = graphQLTestTemplate.postForResource("graphql/behandling/antall_behandlinger.graphql")
        assertThat(response.statusCode.is2xxSuccessful).isTrue
    }

    @Test
    fun `hent egne behandlinger`() {
        val behandling = genererBehandling(1, "test", Behandlingsstatus.UNDER_BEHANDLING, genererSak())
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")
        Mockito.`when`(behandlingRepository.findBySaksbehandlingsansvarligIdentAndStatus(any(), any(), any())).thenReturn(PageImpl(listOf(behandling)))

        val response = graphQLTestTemplate.postForResource("graphql/behandling/hent_egne_behandlinger.graphql")
        assertThat(response.statusCode.is2xxSuccessful).isTrue
        assertThat(response.get("$.data.hentEgneBehandlinger.behandlinger.length()")).isEqualTo("1")
    }

    @Test
    fun `hent egne behandlinger med sortering`() {
        val behandling = genererBehandling(1, "test", Behandlingsstatus.UNDER_BEHANDLING, genererSak())
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")
        Mockito.`when`(behandlingRepository.findBySaksbehandlingsansvarligIdentAndStatus(any(), any(), any())).thenReturn(PageImpl(listOf(behandling)))

        val response = graphQLTestTemplate.postForResource("graphql/behandling/hent_egne_behandlinger_med_sortering.graphql")
        assertThat(response.statusCode.is2xxSuccessful).isTrue
        assertThat(response.get("$.data.hentEgneBehandlinger.behandlinger.length()")).isEqualTo("1")
    }

    @Test
    fun `hent aapne behandlinger`() {
        // given
        val underBehandling = genererBehandling(1, "test", Behandlingsstatus.UNDER_BEHANDLING, genererSak())
        val ikkePaabegynt = genererBehandling(1, "test", Behandlingsstatus.IKKE_PAABEGYNT, genererSak())
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")
        val page = PageImpl(listOf(underBehandling, ikkePaabegynt))
        Mockito.`when`(behandlingRepository.findBehandlingerBegrensetTilBehandlingsstatuser(
            status = org.mockito.kotlin.isNull(),
            dokumentkategori = org.mockito.kotlin.isNull(),
            behandlingstype = org.mockito.kotlin.isNull(),
            gyldigeStatuser = any(),
            inkluderSaksbehandlingansvarlige = any(),
            pageable = any())
        ).thenReturn(page)

        // when
        val response = graphQLTestTemplate.postForResource("graphql/behandling/hent_aapne_behandlinger.graphql")

        // then
        assertThat(response.statusCode.is2xxSuccessful).isTrue
        assertThat(response.get("$.data.hentAapneBehandlinger.behandlinger.length()")).isEqualTo("2")
    }

    @Test
    fun `hent behandling`() {
        val behandling = genererBehandling(1, "test", Behandlingsstatus.UNDER_BEHANDLING, genererSak())
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")
        Mockito.`when`(behandlingRepository.findById(any())).thenReturn(Optional.of(behandling))

        val response = graphQLTestTemplate.postForResource("graphql/behandling/hent_behandling.graphql")
        assertThat(response.statusCode.is2xxSuccessful).isTrue
        assertThat(response.get("$.data.hentBehandling.behandlingId")).isEqualTo("1")
        assertThat(response.get("$.data.hentBehandling.dokumenter.length()")).isEqualTo("0")
    }
}