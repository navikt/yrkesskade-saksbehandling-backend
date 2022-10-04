package no.nav.yrkesskade.saksbehandling.fixtures

import com.expediagroup.graphql.client.jackson.types.JacksonGraphQLError
import com.expediagroup.graphql.client.jackson.types.JacksonGraphQLResponse
import com.expediagroup.graphql.client.jackson.types.JacksonGraphQLSourceLocation
import com.expediagroup.graphql.client.types.GraphQLClientResponse
import com.expediagroup.graphql.generated.HentIdenter
import com.expediagroup.graphql.generated.HentPerson
import com.expediagroup.graphql.generated.enums.IdentGruppe
import com.expediagroup.graphql.generated.hentidenter.IdentInformasjon
import com.expediagroup.graphql.generated.hentidenter.Identliste


fun okResponsPersonFraPdl(): GraphQLClientResponse<HentPerson.Result> {
    return JacksonGraphQLResponse(
        data = HentPerson.Result(gyldigPersonMedNavnOgVegadresse()),
        errors = null,
        extensions = emptyMap()
    )
}

fun okResponsPersonMedMatrikkeladresseFraPdl(): GraphQLClientResponse<HentPerson.Result> {
    return JacksonGraphQLResponse(
        data = HentPerson.Result(gyldigPersonMedNavnOgMatrikkeladresse()),
        errors = null,
        extensions = emptyMap()
    )
}

fun okResponsPersonMedUkjentBostedFraPdl(): GraphQLClientResponse<HentPerson.Result> {
    return JacksonGraphQLResponse(
        data = HentPerson.Result(gyldigPersonMedUkjentBosted()),
        errors = null,
        extensions = emptyMap()
    )
}

fun okResponsPersonUtenBostedsadresseFraPdl(): GraphQLClientResponse<HentPerson.Result> {
    return JacksonGraphQLResponse(
        data = HentPerson.Result(gyldigPersonMedNavnMenUtenBostedsadresse()),
        errors = null,
        extensions = emptyMap()
    )
}

fun okResponsPersonMedEnkelUtenlandskAdresseFraPdl(): GraphQLClientResponse<HentPerson.Result> {
    return JacksonGraphQLResponse(
        data = HentPerson.Result(gyldigPersonMedEnkelUtenlandskAdresse()),
        errors = null,
        extensions = emptyMap()
    )
}

fun okResponsPersonMedUtenlandskAdresseFraPdl(): GraphQLClientResponse<HentPerson.Result> {
    return JacksonGraphQLResponse(
        data = HentPerson.Result(gyldigPersonMedUtenlandskAdresse()),
        errors = null,
        extensions = emptyMap()
    )
}

/**
 * Response med død person
 */
fun okResponsDoedPersonFraPdl(): GraphQLClientResponse<HentPerson.Result> {
    return JacksonGraphQLResponse(
        data = HentPerson.Result(doedPerson()),
        errors = null,
        extensions = emptyMap()
    )
}

/**
 * Response med fortrolig person (kode 7)
 */
fun okResponsFortroligPersonFraPdl(): GraphQLClientResponse<HentPerson.Result> {
    return JacksonGraphQLResponse(
        data = HentPerson.Result(gyldigFortroligPersonMedNavnOgVegadresse()),
        errors = null,
        extensions = emptyMap()
    )
}

/**
 * Response med strengt fortrolig person (kode 6)
 */
fun okResponsStrengtFortroligPersonFraPdl(): GraphQLClientResponse<HentPerson.Result> {
    return JacksonGraphQLResponse(
        data = HentPerson.Result(gyldigStrengtFortroligPersonMedNavnOgVegadresse()),
        errors = null,
        extensions = emptyMap()
    )
}

fun hentIdenterErrorRespons(): GraphQLClientResponse<HentIdenter.Result> {
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

fun okResponsHentIdenterMedFnrUtenHistorikk(): GraphQLClientResponse<HentIdenter.Result> {
    return JacksonGraphQLResponse(
        data = hentIdenterResultMedFnrUtenHistorikk(),
        errors = null,
        extensions = emptyMap()
    )
}

fun okResponsHentIdenterMedAktoerIdUtenHistorikk(): GraphQLClientResponse<HentIdenter.Result> {
    return JacksonGraphQLResponse(
        data = hentIdenterResultMedAktoerIdUtenHistorikk(),
        errors = null,
        extensions = emptyMap()
    )
}

fun hentIdenterResultMedFnrUtenHistorikk(): HentIdenter.Result {
    return HentIdenter.Result(gyldigIdentlisteMedFnrUtenHistorikk())
}

fun hentIdenterResultMedAktoerIdUtenHistorikk(): HentIdenter.Result {
    return HentIdenter.Result(gyldigIdentlisteMedAktoerIdUtenHistorikk())
}

fun gyldigIdentlisteMedFnrUtenHistorikk() = Identliste(listOf(identInformasjon_Fnr()))

fun gyldigIdentlisteMedAktoerIdUtenHistorikk() = Identliste(listOf(identInformasjon_aktoerId()))

fun identInformasjon_aktoerId() = IdentInformasjon(
    ident = "44444444",
    historisk = false,
    gruppe = IdentGruppe.AKTORID
)

fun identInformasjon_Fnr() = IdentInformasjon(
    ident = "33333333333",
    historisk = false,
    gruppe = IdentGruppe.FOLKEREGISTERIDENT
)

