package no.nav.yrkesskade.saksbehandling.hendelser.oppgave.model

import java.util.Objects

enum class Oppgavestatus {
    OPPRETTET, AAPNET, UNDER_BEHANDLING, FERDIGSTILT, FEILREGISTRERT;

    companion object {
        fun getIdFor(status: Oppgavestatus?): Long? {
            return when (status) {
                OPPRETTET -> 1L
                AAPNET -> 2L
                UNDER_BEHANDLING -> 3L
                FERDIGSTILT -> 4L
                FEILREGISTRERT -> 5L
                else -> null
            }
        }

        fun getIdsFor(statuskategori: Oppgavestatuskategori?): List<Long?> {
            return if (Objects.equals(Oppgavestatuskategori.AAPEN, statuskategori)) {
                listOf(getIdFor(OPPRETTET), getIdFor(AAPNET), getIdFor(UNDER_BEHANDLING))
            } else {
                listOf(getIdFor(FERDIGSTILT), getIdFor(FEILREGISTRERT))
            }
        }

        fun avsluttet(): List<Oppgavestatus> {
            return listOf(FERDIGSTILT, FEILREGISTRERT)
        }

        fun aapen(): List<Oppgavestatus> {
            return listOf(OPPRETTET, AAPNET, UNDER_BEHANDLING)
        }
    }
}