package no.nav.yrkesskade.saksbehandling.client.saf

interface ISafRestClient {
    fun hentDokument(journalpostId: String, dokumentinfoId: String): String
}