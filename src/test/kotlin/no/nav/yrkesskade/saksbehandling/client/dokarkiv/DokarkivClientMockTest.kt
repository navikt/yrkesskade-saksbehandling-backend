package no.nav.yrkesskade.saksbehandling.client.dokarkiv

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import no.nav.yrkesskade.saksbehandling.fixtures.ferdigstillJournalpostRequest
import no.nav.yrkesskade.saksbehandling.util.TokenUtil
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@ExtendWith(MockKExtension::class)
internal class DokarkivClientMockTest {

    private lateinit var dokarkivClient: DokarkivClient

    @MockK(relaxed = true)
    lateinit var tokenUtilMock: TokenUtil

    @Test
    fun `ferdigstillJournalpost skal håndtere OK-respons`() {
        dokarkivClient = DokarkivClient(
            createShortCircuitWebClientWithStatus(
                jacksonObjectMapper().writeValueAsString(""),
                HttpStatus.OK
            ),
            tokenUtilMock,
            "mock"
        )
        dokarkivClient.ferdigstillJournalpost(ferdigstillJournalpostRequest())
    }

}


fun createShortCircuitWebClientWithStatus(jsonResponse: String, status: HttpStatus): WebClient {
    val clientResponse: ClientResponse = ClientResponse
        .create(status)
        .header("Content-Type", "application/json")
        .body(jsonResponse).build()

    return WebClient.builder()
        .exchangeFunction { Mono.just(clientResponse) }
        .build()
}
