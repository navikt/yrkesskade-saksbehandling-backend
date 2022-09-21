package no.nav.yrkesskade.saksbehandling.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.servers.Server


@OpenAPIDefinition(
    info = Info(
        title = "YS-skademelding API Doc",
        description = "Definisjonen på innsending av skademelding og yrkessykdom ved hjelp av REST API.",
        contact = Contact(
            name = "Team Yrkesskade",
            email = "yrkesskade@nav.no"
        ),
    ), servers = [Server(url = "https://yrkesskade-melding-api.intern.nav.no/api", description = "Produksjon intern NAV"),
        Server(url = "https://yrkesskade-melding-api.dev.intern.nav.no/api", description = "Utvikling intern NAV")]
)
internal class OpenAPIConfig