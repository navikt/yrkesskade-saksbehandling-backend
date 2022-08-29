package no.nav.yrkesskade.saksbehandling.client.saf

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class LocalSafRestClientTest {

    @Test
    fun `hent dokument`() {
        val dokumentSomBase64 = LocalSafRestClient().hentDokument("1","1")
        assertThat(dokumentSomBase64).isNotNull
    }

    @Test
    fun `hent dokument som kaster Exception`() {
        assertThrows<SafException> {
            LocalSafRestClient().hentDokument("-1", "1")
        }
    }
}