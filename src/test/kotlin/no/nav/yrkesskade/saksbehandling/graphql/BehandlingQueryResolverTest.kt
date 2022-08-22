package no.nav.yrkesskade.saksbehandling.graphql

import com.graphql.spring.boot.test.GraphQLTest
import com.graphql.spring.boot.test.GraphQLTestTemplate
import no.nav.yrkesskade.saksbehandling.config.GraphQLScalarsConfig
import no.nav.yrkesskade.saksbehandling.fixtures.genererBehandling
import no.nav.yrkesskade.saksbehandling.fixtures.genererSak
import no.nav.yrkesskade.saksbehandling.model.Behandlingsstatus
import no.nav.yrkesskade.saksbehandling.repository.BehandlingRepository
import no.nav.yrkesskade.saksbehandling.security.AutentisertBruker
import no.nav.yrkesskade.saksbehandling.test.AbstractTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import

@Import(value = [GraphQLScalarsConfig::class])
@GraphQLTest
class BehandlingQueryResolverTest : AbstractTest() {

    @Autowired
    lateinit var graphQLTestTemplate: GraphQLTestTemplate

    @MockBean
    lateinit var behandlingRepository: BehandlingRepository

    @MockBean
    lateinit var autentisertBruker: AutentisertBruker
    
    @Test
    fun `hent antall behandlinger`() {
        Mockito.`when`(behandlingRepository.count()).thenReturn(1)

        val response = graphQLTestTemplate.postForResource("graphql/antall_behandlinger.graphql")
        assertThat(response.statusCode.is2xxSuccessful).isTrue
    }

    @Test
    fun `hent egne behandlinger`() {
        val behandling = genererBehandling(1, "test", Behandlingsstatus.UNDER_BEHANDLING, genererSak())
        Mockito.`when`(autentisertBruker.preferredUsername).thenReturn("test")
        Mockito.`when`(behandlingRepository.findBySaksbehandlingsansvarligIdent(any(), any())).thenReturn(listOf(behandling))

        val response = graphQLTestTemplate.postForResource("graphql/hent_egne_behandlinger.graphql")
        assertThat(response.statusCode.is2xxSuccessful).isTrue
        assertThat(response.get("$.data.hentEgneBehandlinger.length()")).isEqualTo("1")
    }
}