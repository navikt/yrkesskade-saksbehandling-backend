package no.nav.yrkesskade.saksbehandling.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("rolle")
class RolleConfig(
    @Value("\${rolle.saksbehandler}")
    val SAKSBEHANDLER_ROLLE: String,
    @Value("\${rolle.kode6}")
    val KODE6: String,
    @Value("\${rolle.kode7}")
    val KODE7: String
)
