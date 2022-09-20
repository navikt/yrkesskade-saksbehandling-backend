package no.nav.yrkesskade.saksbehandling.api.v1.brev

import io.swagger.v3.oas.annotations.Parameter
import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.yrkesskade.saksbehandling.model.Brev
import no.nav.yrkesskade.saksbehandling.security.ISSUER
import no.nav.yrkesskade.saksbehandling.service.BrevService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@ProtectedWithClaims(issuer = ISSUER)
@RestController
@RequestMapping(path = ["/api/v1/brev"], produces = [MediaType.APPLICATION_JSON_VALUE])
class BrevController(
    private val brevService: BrevService
) {

    @PostMapping(path = ["/generer"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun genererForhaandsvisning(@Parameter(description = "brev data som skal brukes til å generere PDF") @RequestBody brev: Brev) : String {
        return brevService.genererBrev(brev)
    }
}