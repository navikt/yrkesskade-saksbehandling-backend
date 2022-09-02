package no.nav.yrkesskade.saksbehandling.client

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClientResponseException

internal class AbstractRestClientTest {

    @Test
    fun `test ok`() {
        val test = TestAbstractRestClientImpl().test()
        assertThat(test).isEqualTo("test ok")
    }

    @Test
    fun `test med WebClientResponseException`() {
        assertThrows<WebClientResponseException> {
            TestAbstractRestClientImpl().testWebClientResponseException()
        }
    }

    @Test
    fun `test med RuntimeException`() {
        assertThrows<RuntimeException> {
            TestAbstractRestClientImpl().testRuntimeException()
        }
    }

    private class TestAbstractRestClientImpl : AbstractRestClient("TestClient") {

        fun test() : String {
            return logTimingAndWebClientResponseException("test") {
                "test ok"
            }
        }

        fun testWebClientResponseException() : String {
            return logTimingAndWebClientResponseException("testWebClientResponseException") {
                throw WebClientResponseException(HttpStatus.BAD_REQUEST.value(), "Feil", null, null, null)
            }
        }

        fun testRuntimeException() : String {
            return logTimingAndWebClientResponseException("testRuntimeException") {
                throw RuntimeException("Feil")
            }
        }
    }
}
