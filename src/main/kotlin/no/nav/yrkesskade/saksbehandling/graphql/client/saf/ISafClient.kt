package no.nav.yrkesskade.saksbehandling.graphql.client.saf

import com.expediagroup.graphql.generated.Journalpost

interface ISafClient {
    fun hentOppdatertJournalpost(journalpostId: String): Journalpost.Result?
}