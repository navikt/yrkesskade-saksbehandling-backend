package no.nav.yrkesskade.saksbehandling.config

import no.nav.security.token.support.core.configuration.MultiIssuerConfiguration
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.security.token.support.core.validation.JwtTokenValidationHandler
import no.nav.security.token.support.filter.JwtTokenValidationFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletResponse


private const val GRAPHQL_URL = "/api/graphql"

@Configuration
class WebSecurityConfig {

    /**
     * Filter som tar en ferdigutfylt [TokenValidationContextHolder] (fra [graphqlOidcTokenValidationContextFilter])
     * og returnerer 401 unauthorized dersom det ikke foreligger gyldig token.
     * Filteret har lavest presedens og vil dermed alltid kjøre etter [graphqlOidcTokenValidationContextFilter].
     */
    @Bean
    fun graphqlOidcTokenValidationFilter(
        oidcRequestContextHolder: TokenValidationContextHolder,
        environment: Environment
    ): FilterRegistrationBean<TokenValidationFilter>? {
        return FilterRegistrationBean(TokenValidationFilter(oidcRequestContextHolder, environment)).apply {
            addUrlPatterns(GRAPHQL_URL)
            order = Ordered.LOWEST_PRECEDENCE
        }
    }
}


class TokenValidationFilter(val oidcRequestContextHolder: TokenValidationContextHolder, val environment: Environment) : Filter {
    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, filterChain: FilterChain) {
        if (environment.activeProfiles.contains("local") && servletRequest.parameterMap.containsKey("codegen")) {
            // Vi er i local kjøring og utfører en kodegenering av skjema.
            filterChain.doFilter(servletRequest, servletResponse)
            return
        }
        if (!oidcRequestContextHolder.tokenValidationContext.hasValidToken()) {
            (servletResponse as HttpServletResponse).sendError(HttpStatus.UNAUTHORIZED.value())
        } else {
            filterChain.doFilter(servletRequest, servletResponse)
        }
    }
}