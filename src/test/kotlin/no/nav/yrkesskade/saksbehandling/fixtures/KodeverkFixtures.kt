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

fun behandlingsstatus(): Map<String, KodeverdiDto> = mapOf(
    "ikkePaabegynt" to KodeverdiDto("ikkePaabegynt", "Ikke påbegynt"),
    "underBehandling" to KodeverdiDto("underBehandling", "Under behandling"),
    "ferdig" to KodeverdiDto("ferdig", "Ferdig"),
)

fun framdriftsstatus(): Map<String, KodeverdiDto> = mapOf(
    "ikkePaabegynt" to KodeverdiDto("ikkePaabegynt", "Ikke påbegynt"),
    "underArbeid" to KodeverdiDto("underArbeid", "Under arbeid"),
    "paaVent" to KodeverdiDto("paaVent", "På vent"),
    "avventerSvar" to KodeverdiDto("avventerSvar", "Avventer svar")
)

fun dokumentkategori(): Map<String, KodeverdiDto> = mapOf(
    "tannlegeerklaering" to KodeverdiDto("tannlegeerklaering", "Tannlegeerklæring"),
    "veiledningsbrevTannlegeerklaering" to KodeverdiDto("veiledningsbrevTannlegeerklaering", "Veiledningsbrev Tannlegeerklæring"),
    "veiledningsbrevArbeidstilsynsmelding" to KodeverdiDto("veiledningsbrevArbeidstilsynsmelding", "Veiledningsbrev Arbeidstilsynsmelding")
)