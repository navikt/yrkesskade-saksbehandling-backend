package no.nav.yrkesskade.saksbehandling.graphql.client.pdl

import com.expediagroup.graphql.client.spring.GraphQLWebClient
import com.expediagroup.graphql.generated.HentIdenter
import com.expediagroup.graphql.generated.enums.IdentGruppe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import no.nav.yrkesskade.saksbehandling.fixtures.hentIdenterErrorRespons
import no.nav.yrkesskade.saksbehandling.fixtures.hentIdenterResultMedFnrUtenHistorikk
import no.nav.yrkesskade.saksbehandling.fixtures.identInformasjon_aktoerId
import no.nav.yrkesskade.saksbehandling.fixtures.okResponsHentIdenterMedAktoerIdUtenHistorikk
import no.nav.yrkesskade.saksbehandling.fixtures.okResponsHentIdenterMedFnrUtenHistorikk
import no.nav.yrkesskade.saksbehandling.util.TokenUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.util.ReflectionTestUtils

@ExtendWith(MockKExtension::class)
internal class PdlClientTest {

    private lateinit var client: PdlClient

    @MockK
    lateinit var graphQLWebClient: GraphQLWebClient

    @MockK
    lateinit var tokenUtilMock: TokenUtil

    @BeforeEach
    fun setUp() {
        every { tokenUtilMock.getAppAccessOnBehalfOfTokenWithPdlScope() } returns "abc"
        client = PdlClient("test", tokenUtilMock)
        ReflectionTestUtils.setField(client, "client", graphQLWebClient)
    }

    @Test
    fun hentAktorId() {
        coEvery { graphQLWebClient.execute<HentIdenter.Result>(any(), any()) } returns okResponsHentIdenterMedAktoerIdUtenHistorikk()
        val aktorId = client.hentAktorId("01010112345")
        assertThat(aktorId).isEqualTo(identInformasjon_aktoerId().ident)
    }

    @Test
    fun hentIdenter() {
        coEvery { graphQLWebClient.execute<HentIdenter.Result>(any(), any()) } returns okResponsHentIdenterMedFnrUtenHistorikk()
        val hentIdenterResult = client.hentIdenter("12345", listOf(IdentGruppe.FOLKEREGISTERIDENT), false)
        assertThat(hentIdenterResult).isEqualTo(hentIdenterResultMedFnrUtenHistorikk())
    }

    @Test
    fun `hentIdenter skal kaste exception naar PDL gir error respons`() {
        coEvery { graphQLWebClient.execute<HentIdenter.Result>(any(), any()) } returns hentIdenterErrorRespons()
        Assertions.assertThrows(RuntimeException::class.java) {
            client.hentIdenter("12345", listOf(IdentGruppe.FOLKEREGISTERIDENT), false)
        }
    }
}