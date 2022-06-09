package no.nav.yrkesskade.saksbehandling.util

import no.nav.security.token.support.core.context.TokenValidationContextHolder
import org.springframework.stereotype.Component

const val LEVEL = "acr=Level4"
const val ISSUER = "tokenx"
const val PID = "pid"



// denne gangen ikke fødselsnr., men epostadresse




@Component
class AutentisertBruker(
    val tokenValidationContextHolder: TokenValidationContextHolder
) {
    val fodselsnummer: String
        get() {
            val claimSet = tokenValidationContextHolder
                .tokenValidationContext.getClaims(ISSUER)
            return if (claimSet.get(PID) == null) {
                claimSet.subject
            } else {
                claimSet.get(PID) as String
            }
        }

    val jwtToken: String
        get() =
            tokenValidationContextHolder
                .tokenValidationContext
                .getJwtToken(ISSUER)
                .tokenAsString
}