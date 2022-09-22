package no.nav.yrkesskade.saksbehandling.util

import no.bekk.bekkopen.date.NorwegianDateUtil
import no.nav.yrkesskade.saksbehandling.model.Behandlingstype
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

/**
 * Hjelpeklasse som beregner frist for behandling av en oppgave ut fra en gitt dato.
 * Hvis tidspunkt for den gitte datoen før klokken 12, er fristen neste virkedag.
 * Hvis tidspunkt for den gitte datoen etter klokken 12, er fristen om to virkedager.
 *
 * Bruker biblioteket [NorwegianDateUtil] som tar hensyn til norske fri-/helligdager.
 * Hentet fra prosjektet jfr-manuell og noe refaktorert.
 */
object FristFerdigstillelseTimeManager {

    fun nesteGyldigeFristForFerdigstillelseInstant(behandlingstype: Behandlingstype, tidspunkt : Instant): Instant {

        return when (behandlingstype) {
            Behandlingstype.JOURNALFOERING -> tidspunkt.plus(2, ChronoUnit.DAYS)
            Behandlingstype.VEILEDNING -> tidspunkt.plus(25, ChronoUnit.DAYS)
            else -> tidspunkt.plus(30, ChronoUnit.DAYS)
        }
    }

    fun nesteGyldigeFristForFerdigstillelseLocalDate(behandlingstype: Behandlingstype, tidspunkt : Instant): LocalDate {
        return nesteGyldigeFristForFerdigstillelseInstant(behandlingstype, tidspunkt).atZone(ZoneId.systemDefault()).toLocalDate()
    }

    fun nesteGyldigeFristForFerdigstillelseLocalDateTime(behandlingstype: Behandlingstype, tidspunkt : Instant): LocalDateTime {
        return nesteGyldigeFristForFerdigstillelseInstant(behandlingstype, tidspunkt).atZone(ZoneId.systemDefault()).toLocalDateTime()
    }

}