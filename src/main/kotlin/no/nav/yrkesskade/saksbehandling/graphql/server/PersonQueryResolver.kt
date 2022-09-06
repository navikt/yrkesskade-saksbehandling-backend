package no.nav.yrkesskade.saksbehandling.graphql.server

import graphql.kickstart.tools.GraphQLQueryResolver
import no.nav.yrkesskade.saksbehandling.service.PersonService
import org.springframework.stereotype.Component

@Component
class PersonQueryResolver(
    private val personService: PersonService
)  : GraphQLQueryResolver {

    fun hentPerson(foedselsnummer: String) = personService.hentPerson(foedselsnummer)
}