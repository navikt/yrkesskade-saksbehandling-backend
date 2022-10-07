package no.nav.yrkesskade.saksbehandling.fixtures

import com.expediagroup.graphql.client.jackson.types.JacksonGraphQLError
import com.expediagroup.graphql.client.jackson.types.JacksonGraphQLResponse
import com.expediagroup.graphql.client.jackson.types.JacksonGraphQLSourceLocation
import com.expediagroup.graphql.client.types.GraphQLClientResponse
import com.expediagroup.graphql.generated.Journalpost
import no.nav.yrkesskade.saksbehandling.fixtures.journalpost.gyldigJournalpostMedAktoerId
import no.nav.yrkesskade.saksbehandling.fixtures.journalpost.gyldigJournalpostMedAktoerIdUtenDokumenter

fun errorRespons(): GraphQLClientResponse<Journalpost.Result> {
    return JacksonGraphQLResponse(
        data = null,
        errors = listOf(
            JacksonGraphQLError(
                message = "Validation error",
                locations = listOf(JacksonGraphQLSourceLocation(line = 1, column = 1))
            )
        ),
        extensions = emptyMap()
    )
}

fun okRespons(): GraphQLClientResponse<Journalpost.Result> {
    return okRespons(gyldigJournalpostMedAktoerId())
}

fun okRespons(journalpost: com.expediagroup.graphql.generated.journalpost.Journalpost): GraphQLClientResponse<Journalpost.Result> {
    return JacksonGraphQLResponse(
        data = Journalpost.Result(journalpost),
        errors = null,
        extensions = emptyMap()
    )
}

fun okResponsUtenDokumenter(): GraphQLClientResponse<Journalpost.Result> {
    return JacksonGraphQLResponse(
        data = Journalpost.Result(gyldigJournalpostMedAktoerIdUtenDokumenter()),
        errors = null,
        extensions = emptyMap()
    )
}