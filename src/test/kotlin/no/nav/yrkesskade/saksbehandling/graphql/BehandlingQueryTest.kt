package no.nav.yrkesskade.saksbehandling.graphql

import com.graphql.spring.boot.test.GraphQLTest
import com.graphql.spring.boot.test.GraphQLTestTemplate
import no.nav.security.token.support.core.configuration.MultiIssuerConfiguration
import no.nav.yrkesskade.saksbehandling.config.GraphQLScalarsConfig
import no.nav.yrkesskade.saksbehandling.config.WebSecurityConfig
import no.nav.yrkesskade.saksbehandling.repository.BehandlingRepository
import no.nav.yrkesskade.saksbehandling.test.AbstractTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import

@Import(GraphQLScalarsConfig::class, WebSecurityConfig::class, MultiIssuerConfiguration::class)
@GraphQLTest(profiles = ["integration"], webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class BehandlingQueryTest : AbstractTest() {

    @Autowired
    lateinit var graphQLTestTemplate: GraphQLTestTemplate

    @MockBean
    lateinit var behandlingRepository: BehandlingRepository

    @Test
    fun `hent antall behandlinger uten token skal gi 401-respons`() {
        Mockito.`when`(behandlingRepository.count()).thenReturn(1)

        val response = graphQLTestTemplate.postForResource("graphql/antall_behandlinger.graphql")
        assertThat(response.statusCode.is4xxClientError).isTrue
    }
}