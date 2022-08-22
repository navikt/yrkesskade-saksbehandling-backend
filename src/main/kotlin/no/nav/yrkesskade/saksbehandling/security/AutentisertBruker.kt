package no.nav.yrkesskade.saksbehandling.security
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import org.springframework.stereotype.Component

const val ISSUER = "azuread"
const val PID = "preferred_username"

@Component
class AutentisertBruker(
    val tokenValidationContextHolder: TokenValidationContextHolder
) {
    val preferredUsername: String
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