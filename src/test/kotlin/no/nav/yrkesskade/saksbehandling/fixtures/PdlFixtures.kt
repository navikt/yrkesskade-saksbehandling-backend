package no.nav.yrkesskade.meldingmottak.fixtures

import com.expediagroup.graphql.client.jackson.types.JacksonGraphQLResponse
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

fun hentIdenterResultMedFnrUtenHistorikk(): HentIdenter.Result {
    return HentIdenter.Result(gyldigIdentlisteUtenFnrHistorikk())
}

fun gyldigIdentlisteUtenFnrHistorikk() = Identliste(listOf(identInformasjon_Fnr()))

fun identInformasjon_Fnr() = IdentInformasjon(
    ident = "33333333333",
    historisk = false,
    gruppe = IdentGruppe.FOLKEREGISTERIDENT
)