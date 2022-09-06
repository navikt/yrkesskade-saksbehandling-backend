package no.nav.yrkesskade.saksbehandling.graphql.client.pdl

import com.expediagroup.graphql.generated.HentIdenter
import com.expediagroup.graphql.generated.enums.IdentGruppe
import com.expediagroup.graphql.generated.hentperson.Person

interface IPdlClient {
    fun hentAktorId(fodselsnummer: String): String?
    fun hentPerson(foedselsnummer: String): Person?
    fun hentIdenter(ident: String, grupper: List<IdentGruppe>, historikk: Boolean = false): HentIdenter.Result?
}