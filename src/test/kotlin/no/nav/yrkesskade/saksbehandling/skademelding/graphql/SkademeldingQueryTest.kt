package no.nav.yrkesskade.saksbehandling.skademelding.graphql

import com.graphql.spring.boot.test.GraphQLTest
import com.graphql.spring.boot.test.GraphQLTestTemplate
import no.nav.yrkesskade.saksbehandling.fixtures.skademelding.enkelSkademeldingEntity
import no.nav.yrkesskade.saksbehandling.skademelding.repository.SkademeldingDao
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@GraphQLTest
class SkademeldingQueryTest {

    @Autowired
    lateinit var graphQLTestTemplate: GraphQLTestTemplate

    @MockBean
    lateinit var skademeldingDao: SkademeldingDao

    @Test
    fun `hent skademeldinger`() {
        val skademeldingEntity = enkelSkademeldingEntity()

        Mockito.`when`(skademeldingDao.findAll(any(PageRequest::class.java))).thenReturn(
            PageImpl(
                listOf(skademeldingEntity)
            )
        )

        val response = graphQLTestTemplate.postForResource("graphql/skademelding/hent_skademeldinger.graphql")

        assertThat(response.isOk).isTrue()
        assertThat(response["$.data.hentSkademeldinger.length()"]).isEqualTo("1")
        assertThat(response["$.data.hentSkademeldinger[0].innmelderIdentitetsnummer"]).isEqualTo("012334567891")
        assertThat(response["$.data.hentSkademeldinger[0].skadelidtIdentitetsnummer"]).isEqualTo("12345678910")
    }

    @Test
    fun `hent skademeldinger for skadelidt - har skademeldinger`() {
        val skademeldingEntity = enkelSkademeldingEntity()

        Mockito.`when`(skademeldingDao.findBySkadelidtIdentitetsnummer(anyString())).thenReturn(
            listOf(skademeldingEntity)
        )

        val response = graphQLTestTemplate.postForResource("graphql/skademelding/hent_skademeldinger_for_skadelidt.graphql")

        assertThat(response.isOk).isTrue()
        assertThat(response["$.data.hentSkademeldingerForSkadelidt.length()"]).isEqualTo("1")
        assertThat(response["$.data.hentSkademeldingerForSkadelidt[0].innmelderIdentitetsnummer"]).isEqualTo("012334567891")
        assertThat(response["$.data.hentSkademeldingerForSkadelidt[0].skadelidtIdentitetsnummer"]).isEqualTo("12345678910")
    }

    @Test
    fun `hent skademeldinger for skadelidt - har ingen skademeldinger`() {

        Mockito.`when`(skademeldingDao.findBySkadelidtIdentitetsnummer(anyString())).thenReturn(
            emptyList()
        )

        val response = graphQLTestTemplate.postForResource("graphql/skademelding/hent_skademeldinger_for_skadelidt.graphql")

        assertThat(response.isOk).isTrue()
        assertThat(response["$.data.hentSkademeldingerForSkadelidt.length()"]).isEqualTo("0")
    }

    @Test
    fun `hent alle skademeldinger`() {
        val skademeldingEntity = enkelSkademeldingEntity()

        Mockito.`when`(skademeldingDao.findAll()).thenReturn(
            listOf(skademeldingEntity)
        )

        val response = graphQLTestTemplate.postForResource("graphql/skademelding/hent_alle_skademeldinger.graphql")

        assertThat(response.isOk).isTrue()
        assertThat(response["$.data.hentAlleSkademeldinger.length()"]).isEqualTo("1")
        assertThat(response["$.data.hentAlleSkademeldinger[0].innmelderIdentitetsnummer"]).isEqualTo("012334567891")
        assertThat(response["$.data.hentAlleSkademeldinger[0].skadelidtIdentitetsnummer"]).isEqualTo("12345678910")
    }

    @Test
    fun `hent antall skademeldinger`() {
        Mockito.`when`(skademeldingDao.count()).thenReturn(10)

        val response = graphQLTestTemplate.postForResource("graphql/skademelding/antall_skademeldinger.graphql")

        assertThat(response.isOk).isTrue()
        assertThat(response["$.data.antallSkademeldinger"]).isEqualTo("10")
    }
}