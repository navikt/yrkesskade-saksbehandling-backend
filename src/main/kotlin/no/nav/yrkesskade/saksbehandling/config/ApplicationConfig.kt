package no.nav.yrkesskade.saksbehandling.config

import no.nav.security.token.support.client.spring.oauth2.EnableOAuth2Client
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Configuration


@EnableOAuth2Client
@EnableJwtTokenValidation(
    ignore = ["org.springframework", "org.springdoc"]
)
@ConfigurationPropertiesScan
@Configuration
class ApplicationConfig