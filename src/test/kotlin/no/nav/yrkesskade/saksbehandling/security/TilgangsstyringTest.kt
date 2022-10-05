package no.nav.yrkesskade.saksbehandling.security

import io.mockk.every
import io.mockk.mockk
import no.nav.yrkesskade.saksbehandling.config.RolleConfig
import no.nav.yrkesskade.saksbehandling.util.Tilgang
import no.nav.yrkesskade.saksbehandling.util.Tilgangsstyring
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class TilgangsstyringTest {

    private val SAKSBEHANDLER_GROUPID= "saksbehandler_groupId"
    private val autentisertBruker: AutentisertBruker = mockk()
    private lateinit var rolleConfig: RolleConfig
    private lateinit var tilgangsstyring: Tilgangsstyring

    @Test
    fun `autentisert bruker med saksbehandler gruppe skal kunne lese dokument`() {
        // given
        every { autentisertBruker.groups } returns listOf(SAKSBEHANDLER_GROUPID)
        rolleConfig = RolleConfig(SAKSBEHANDLER_ROLLE = SAKSBEHANDLER_GROUPID)
        tilgangsstyring = Tilgangsstyring(autentisertBruker, rolleConfig)

        // when
        val harTilgang = tilgangsstyring.harTilgang(Tilgang.LESE_DOKUMENT)

        // then
        assertThat(harTilgang).isTrue
    }

    @Test
    fun `autentisert bruker med saksbehandler gruppe skal kunne produsere dokument`() {
        // given
        every { autentisertBruker.groups } returns listOf(SAKSBEHANDLER_GROUPID)
        rolleConfig = RolleConfig(SAKSBEHANDLER_ROLLE = SAKSBEHANDLER_GROUPID)
        tilgangsstyring = Tilgangsstyring(autentisertBruker, rolleConfig)

        // when
        val harTilgang = tilgangsstyring.harTilgang(Tilgang.PRODUSERE_DOKUMENT)

        // then
        assertThat(harTilgang).isTrue
    }

    @Test
    fun `autentisert bruker med ukjent gruppe skal ikke kunne produsere dokument`() {
        // given
        every { autentisertBruker.groups } returns listOf("ukjent gruppe")
        rolleConfig = RolleConfig(SAKSBEHANDLER_ROLLE = SAKSBEHANDLER_GROUPID)
        tilgangsstyring = Tilgangsstyring(autentisertBruker, rolleConfig)

        // when
        val harTilgang = tilgangsstyring.harTilgang(Tilgang.PRODUSERE_DOKUMENT)

        // then
        assertThat(harTilgang).isFalse
    }

    @Test
    fun `autentisert bruker med ukjent gruppe skal ikke kunne lese dokument`() {
        // given
        every { autentisertBruker.groups } returns listOf("ukjent gruppe")
        rolleConfig = RolleConfig(SAKSBEHANDLER_ROLLE = SAKSBEHANDLER_GROUPID)
        tilgangsstyring = Tilgangsstyring(autentisertBruker, rolleConfig)

        // when
        val harTilgang = tilgangsstyring.harTilgang(Tilgang.LESE_DOKUMENT)

        // then
        assertThat(harTilgang).isFalse
    }
}