package no.nav.yrkesskade.saksbehandling.client.saf

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class LocalSafRestClientTest {

    val localSafRestClient = LocalSafRestClient()

    @Test
    fun `hent dokument`() {
        val dokumentSomBase64 = localSafRestClient.hentDokument("1","1")
        assertThat(dokumentSomBase64).isNotNull
    }

    @Test
    fun `hent dokument som kaster Exception`() {
        assertThrows<SafException> {
            localSafRestClient.hentDokument("-1", "1")
        }
    }
}