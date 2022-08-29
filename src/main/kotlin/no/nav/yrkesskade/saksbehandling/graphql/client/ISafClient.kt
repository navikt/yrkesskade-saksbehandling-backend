package no.nav.yrkesskade.saksbehandling.graphql.client

import com.expediagroup.graphql.generated.Journalpost

interface ISafClient {
    fun hentOppdatertJournalpost(journalpostId: String): Journalpost.Result?
}