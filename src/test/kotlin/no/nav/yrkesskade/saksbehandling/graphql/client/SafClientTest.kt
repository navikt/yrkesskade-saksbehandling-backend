package no.nav.yrkesskade.saksbehandling.graphql.client

import com.expediagroup.graphql.client.spring.GraphQLWebClient
import com.expediagroup.graphql.generated.Journalpost
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import no.nav.yrkesskade.saksbehandling.fixtures.errorRespons
import no.nav.yrkesskade.saksbehandling.fixtures.journalpostResultWithBrukerAktoerid
import no.nav.yrkesskade.saksbehandling.fixtures.okRespons
import no.nav.yrkesskade.saksbehandling.graphql.client.saf.SafClient
import no.nav.yrkesskade.saksbehandling.util.TokenUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.util.ReflectionTestUtils

@ExtendWith(MockKExtension::class)
internal class SafClientTest {

    private lateinit var client: SafClient

    @MockK
    lateinit var graphQLWebClient: GraphQLWebClient

    @MockK(relaxed = true)
    lateinit var tokenUtilMock: TokenUtil

    @BeforeEach
    fun init() {
        every { tokenUtilMock.getAppAccessTokenWithSafScope() } returns "abc"

        client = SafClient(safGraphqlUrl = "test", "yrkesskade-saksbehandling-backend", tokenUtilMock)
        ReflectionTestUtils.setField(client, "client", graphQLWebClient)
    }

    @Test
    fun `hentOppdatertJournalpost skal kaste exception naar SAF gir error response`() {
        coEvery { graphQLWebClient.execute<Journalpost.Result>(any(), any()) } returns errorRespons()
        Assertions.assertThrows(RuntimeException::class.java) {
            client.hentOppdatertJournalpost("123123")
        }
    }

    @Test
    fun `hentOppdatertJournalpost skal returnere responsen naar SAF gir ok til svar`() {
        coEvery { graphQLWebClient.execute<Journalpost.Result>(any(), any()) } returns okRespons()
        val journalpost = client.hentOppdatertJournalpost("1337")
        assertThat(journalpost).isEqualTo(journalpostResultWithBrukerAktoerid())
    }
}