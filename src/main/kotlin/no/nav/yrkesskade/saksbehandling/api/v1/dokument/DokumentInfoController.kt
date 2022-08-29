package no.nav.yrkesskade.saksbehandling.api.v1.dokument

import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.yrkesskade.saksbehandling.client.saf.ISafRestClient
import no.nav.yrkesskade.saksbehandling.security.ISSUER
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@ProtectedWithClaims(issuer = ISSUER)
@RestController
@RequestMapping(path = ["/api/v1/dokumentinfo"], produces = [MediaType.APPLICATION_JSON_VALUE])
class DokumentInfoController(
    @Qualifier("safRestClient") private val safRestClient: ISafRestClient
) {

    @GetMapping("/{journalpostId}/{dokumentinfoId}")
    fun hentDokument(
        @PathVariable("journalpostId") journalpostId: String,
        @PathVariable("dokumentinfoId") dokumentinfoId: String
    ): String {
        return safRestClient.hentDokument(journalpostId, dokumentinfoId)
    }
}