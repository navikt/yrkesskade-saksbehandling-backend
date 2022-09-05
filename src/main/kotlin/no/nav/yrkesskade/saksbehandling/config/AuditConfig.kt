package no.nav.yrkesskade.saksbehandling.config

import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.yrkesskade.saksbehandling.audit.AuthenticatedUserProvider
import no.nav.yrkesskade.saksbehandling.security.AutentisertBruker
import org.javers.spring.auditable.AuthorProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary


@Configuration
class AuditConfig(val oidcRequestContextHolder: TokenValidationContextHolder) {

    @Primary
    @Bean
    fun provideJaversAuthor(): AuthorProvider {
        val autentisertBruker = AutentisertBruker(oidcRequestContextHolder)
        return AuthenticatedUserProvider(autentisertBruker)
    }

}