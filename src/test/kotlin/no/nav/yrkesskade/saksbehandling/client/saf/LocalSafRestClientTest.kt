package no.nav.yrkesskade.saksbehandling.client.saf

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class LocalSafRestClientTest {

    @Test
    fun `hent dokument`() {
        val localSafRestClient = LocalSafRestClient()
        assertThat(localSafRestClient).isNotNull
        val dokumentSomBase64 = localSafRestClient.hentDokument("1","1")
        assertThat(dokumentSomBase64).isNotNull
    }

    @Test
    fun `hent dokument som kaster Exception`() {
        val localSafRestClient = LocalSafRestClient()
        assertThrows<SafException> {
            localSafRestClient.hentDokument("-1", "1")
        }
    }
}