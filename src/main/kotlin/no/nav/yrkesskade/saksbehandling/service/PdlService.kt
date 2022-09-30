package no.nav.yrkesskade.saksbehandling.service

import com.expediagroup.graphql.generated.HentIdenter
import com.expediagroup.graphql.generated.enums.IdentGruppe
import com.expediagroup.graphql.generated.hentperson.Person
import no.nav.yrkesskade.saksbehandling.graphql.client.pdl.IPdlClient
import no.nav.yrkesskade.saksbehandling.graphql.client.pdl.PdlException
import org.springframework.stereotype.Component

@Component
class PdlService(
    private val pdlClient: IPdlClient
) {

    fun hentAktorId(foedselsnummer: String) = pdlClient.hentAktorId(foedselsnummer)

    fun hentPerson(foedselsnummer: String): Person? = pdlClient.hentPerson(foedselsnummer)

    fun hentFoedselsnummer(aktoerId: String): String {
        val hentIdenter: HentIdenter.Result? =
            pdlClient.hentIdenter(aktoerId, listOf(IdentGruppe.FOLKEREGISTERIDENT), false)
        return extractFoedselsnummer(hentIdenter)
    }

    private fun extractFoedselsnummer(identerResult: HentIdenter.Result?): String {
        return identerResult?.hentIdenter?.identer?.first {
                identInfo -> identInfo.gruppe == IdentGruppe.FOLKEREGISTERIDENT
        }?.ident ?: throw PdlException("Fant ikke fødselsnummer i PDL")
    }
}
