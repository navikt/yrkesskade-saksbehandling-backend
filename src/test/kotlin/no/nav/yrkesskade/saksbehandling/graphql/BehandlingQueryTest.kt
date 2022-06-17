package no.nav.yrkesskade.saksbehandling.graphql

import com.graphql.spring.boot.test.GraphQLTest
import com.graphql.spring.boot.test.GraphQLTestTemplate
import no.nav.yrkesskade.saksbehandling.config.GraphQLScalarsConfig
import no.nav.yrkesskade.saksbehandling.repository.BehandlingRepository
import no.nav.yrkesskade.saksbehandling.test.AbstractTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import

@Import(GraphQLScalarsConfig::class)
@GraphQLTest
class BehandlingQueryTest : AbstractTest() {

    @Autowired
    lateinit var graphQLTestTemplate: GraphQLTestTemplate

    @MockBean
    lateinit var behandlingRepository: BehandlingRepository

    @Test
    fun `hent antall behandlinger`() {
        Mockito.`when`(behandlingRepository.count()).thenReturn(1)

        val response = graphQLTestTemplate.postForResource("graphql/antall_behandlinger.graphql")
        assertThat(response.statusCode.is2xxSuccessful).isTrue
    }
}