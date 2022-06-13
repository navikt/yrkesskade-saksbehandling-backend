package no.nav.yrkesskade.saksbehandling.config

import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Configuration

@EnableJwtTokenValidation
@ConfigurationPropertiesScan
@Configuration
class ApplicationConfig