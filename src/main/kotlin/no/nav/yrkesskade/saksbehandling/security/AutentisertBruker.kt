package no.nav.yrkesskade.saksbehandling.security
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import org.springframework.stereotype.Component

const val ISSUER = "azuread"
const val PID = "preferred_username"
const val GROUPS = "groups"

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

    val groups: List<String>
        get() =
            tokenValidationContextHolder
                .tokenValidationContext
                .getJwtToken(ISSUER)
                .jwtTokenClaims
                .getAsList(GROUPS)

    val jwtToken: String
        get() =
            tokenValidationContextHolder
                .tokenValidationContext
                .getJwtToken(ISSUER)
                .tokenAsString
}