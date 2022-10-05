package no.nav.yrkesskade.saksbehandling.util

import no.nav.yrkesskade.saksbehandling.config.Rolle
import no.nav.yrkesskade.saksbehandling.config.RolleConfig
import no.nav.yrkesskade.saksbehandling.security.AutentisertBruker
import org.springframework.stereotype.Component

@Component
class Tilgangsstyring(
    val autentisertBruker: AutentisertBruker,
    val rolleConfig: RolleConfig
) {

    fun sjekkTilgang(tilgang: Tilgang): Boolean {
        val rolleFraToken = utledRolleFraToken()
        return tilgang.roller.contains(rolleFraToken)
    }

    private fun utledRolleFraToken(): Rolle {
        return when {
            autentisertBruker.groups.contains(rolleConfig.SAKSBEHANDLER_ROLLE) -> Rolle.SAKSBEHANDLER
            else -> Rolle.UKJENT
        }
    }
}

enum class Tilgang(vararg val roller: Rolle) {
    PRODUSERE_DOKUMENT(Rolle.SAKSBEHANDLER),
    LESE_DOKUMENT(Rolle.SAKSBEHANDLER, Rolle.VEILEDER)
}