package no.nav.yrkesskade.saksbehandling.service

import com.expediagroup.graphql.generated.hentperson.Person
import no.nav.yrkesskade.saksbehandling.graphql.client.pdl.IPdlClient
import org.springframework.stereotype.Service

@Service
class PersonService(
    private val pdlService: PdlService
) {

    fun hentPerson(foedselsnummer: String): Person? {
        return pdlService.hentPerson(foedselsnummer)
    }
}