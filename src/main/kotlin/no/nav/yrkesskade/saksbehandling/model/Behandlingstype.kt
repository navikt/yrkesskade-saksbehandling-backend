package no.nav.yrkesskade.saksbehandling.model

enum class Behandlingstype(val verdi: String) {
    JOURNALFOERING("Journalføring"),
    VEILEDNING("Veiledning"),
    KRAV_MELDING("Krav"),
    KLAGE("Klage"),
    ANKE("Anke"),
    INNSYN("Innsyn"),
    GJENOPPRETTING("Gjenoppretting"),
    REVURDERING("Revurdering"),
    TILBAKEKREVING("Tilbakekreving")
}