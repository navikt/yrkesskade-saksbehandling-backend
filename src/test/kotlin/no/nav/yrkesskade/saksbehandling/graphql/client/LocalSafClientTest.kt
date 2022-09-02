package no.nav.yrkesskade.saksbehandling.graphql.client

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class LocalSafClientTest {

    @Test
    fun `hent journalpost`() {
        val safClient = LocalSafClient()
        val journalpost = safClient.hentOppdatertJournalpost("1")
        assertThat(journalpost).isNotNull
        assertThat(journalpost!!.journalpost).isNotNull
        assertThat(journalpost.journalpost!!.journalpostId).isEqualTo("1")
    }
}