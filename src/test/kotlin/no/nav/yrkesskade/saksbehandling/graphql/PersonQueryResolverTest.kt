package no.nav.yrkesskade.saksbehandling.graphql

import com.graphql.spring.boot.test.GraphQLTest
import com.graphql.spring.boot.test.GraphQLTestTemplate
import no.nav.yrkesskade.saksbehandling.fixtures.gyldigPersonMedNavnOgVegadresse
import no.nav.yrkesskade.saksbehandling.config.GraphQLScalarsConfig
import no.nav.yrkesskade.saksbehandling.service.PdlService
import no.nav.yrkesskade.saksbehandling.test.AbstractTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import

@Import(value = [GraphQLScalarsConfig::class, GraphQLConfig::class])
@GraphQLTest
class PersonQueryResolverTest : AbstractTest() {

    @Autowired
    lateinit var graphQLTestTemplate: GraphQLTestTemplate

    @Autowired
    lateinit var pdlService: PdlService

    @Test
    fun `hent person`() {
        Mockito.`when`(pdlService.hentPerson(anyString())).thenReturn(gyldigPersonMedNavnOgVegadresse())

        val response = graphQLTestTemplate.postForResource("graphql/person/hent_person.graphql")
        Assertions.assertThat(response.statusCode.is2xxSuccessful).isTrue
        Assertions.assertThat(response.get("$.data.hentPerson.navn[0].fornavn")).isEqualTo("Ola")
        Assertions.assertThat(response.get("$.data.hentPerson.navn[0].mellomnavn")).isNull()
        Assertions.assertThat(response.get("$.data.hentPerson.navn[0].etternavn")).isEqualTo("Normann")
    }
}