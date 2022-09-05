package no.nav.yrkesskade.saksbehandling.audit

import no.nav.yrkesskade.saksbehandling.security.AutentisertBruker
import org.javers.spring.auditable.AuthorProvider
import org.springframework.stereotype.Component

@Component
class AuthenticatedUserProvider(private val autentisertBruker: AutentisertBruker) : AuthorProvider {
    override fun provide(): String {
        return autentisertBruker.preferredUsername
    }
}