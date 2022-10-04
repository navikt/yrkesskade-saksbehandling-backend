package no.nav.yrkesskade.saksbehandling.service

import com.expediagroup.graphql.generated.HentIdenter
import com.expediagroup.graphql.generated.enums.IdentGruppe
import com.expediagroup.graphql.generated.hentperson.Person
import no.nav.yrkesskade.saksbehandling.graphql.client.pdl.IPdlClient
import no.nav.yrkesskade.saksbehandling.graphql.client.pdl.PdlException
import no.nav.yrkesskade.saksbehandling.util.Tokentype
import org.springframework.stereotype.Component

@Component
class PdlService(
    private val pdlClient: IPdlClient
) {

    fun hentAktorId(foedselsnummer: String) = pdlClient.hentAktorId(foedselsnummer)

    fun hentPerson(foedselsnummer: String): Person? = pdlClient.hentPerson(foedselsnummer)

    fun hentFoedselsnummerMedOnBehalfOfToken(aktoerId: String): String {
        val hentIdenter: HentIdenter.Result? =
            pdlClient.hentIdenter(
                aktoerId,
                listOf(IdentGruppe.FOLKEREGISTERIDENT),
                false,
                Tokentype.ON_BEHALF_OF
            )
        return extractFoedselsnummer(hentIdenter)
    }

    fun hentFoedselsnummerMedMaskinTilMaskinToken(aktoerId: String): String {
        val hentIdenter: HentIdenter.Result? =
            pdlClient.hentIdenter(
                aktoerId,
                listOf(IdentGruppe.FOLKEREGISTERIDENT),
                false,
                Tokentype.MASKIN_TIL_MASKIN
            )
        return extractFoedselsnummer(hentIdenter)
    }

    private fun extractFoedselsnummer(identerResult: HentIdenter.Result?): String {
        return identerResult?.hentIdenter?.identer?.first {
                identInfo -> identInfo.gruppe == IdentGruppe.FOLKEREGISTERIDENT
        }?.ident ?: throw PdlException("Fant ikke fødselsnummer i PDL")
    }
}
