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
    TILBAKEKREVING("Tilbakekreving");

    companion object {
        inline fun valueOfOrNull(name: String): Behandlingstype? {
            return Behandlingstype.values().firstOrNull { it.name == name }
        }
    }
}