package no.nav.yrkesskade.saksbehandling.fixtures

import no.nav.yrkesskade.kodeverk.model.KodeverdiDto

fun behandlingstyper(): Map<String, KodeverdiDto> = mapOf(
    "anke" to KodeverdiDto("anke", "Anke"),
    "gjenoppretting" to KodeverdiDto("gjenoppretting", "Gjenoppretting"),
    "innsyn" to KodeverdiDto("innsyn", "Innsyn"),
    "journalfoering" to KodeverdiDto("journalfoering", "Journalføring"),
    "klage" to KodeverdiDto("klage", "Klage"),
    "krav-melding" to KodeverdiDto("krav-melding", "Krav/Melding"),
    "revurdering" to KodeverdiDto("revurdering", "Revurdering"),
    "tilbakekreving" to KodeverdiDto("tilbakekreving", "Tilbakekreving"),
    "veiledning" to KodeverdiDto("veiledning", "Veiledning")
)
