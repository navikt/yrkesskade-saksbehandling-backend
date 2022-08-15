package no.nav.yrkesskade.saksbehandling.api

import no.nav.security.token.support.core.api.Protected
import no.nav.security.token.support.core.api.Unprotected
import no.nav.yrkesskade.saksbehandling.model.Behandlingstype
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/v1/behandlingstyper"], produces = [MediaType.APPLICATION_JSON_VALUE])
@Unprotected
class BehandlingstypeController {

    @GetMapping
    fun hentBehandlingstyper(): ResponseEntity<Array<Behandlingstype>> {
        return ResponseEntity.ok(Behandlingstype.values())
    }
}