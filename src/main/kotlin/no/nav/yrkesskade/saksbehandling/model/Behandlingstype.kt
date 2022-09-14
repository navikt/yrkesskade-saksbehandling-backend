package no.nav.yrkesskade.saksbehandling.model

enum class Behandlingstype(val kode: String) {
    JOURNALFOERING("journalfoering"),
    VEILEDNING("veiledning"),
    KRAV_MELDING("krav-melding"),
    KLAGE("klage"),
    ANKE("anke"),
    INNSYN("innsyn"),
    GJENOPPRETTING("gjenoppretting"),
    REVURDERING("revurdering"),
    TILBAKEKREVING("tilbakekreving");

    companion object {
        private val map = Behandlingstype.values().associateBy(Behandlingstype::kode)
        inline fun valueOfOrNull(name: String): Behandlingstype? {
            return Behandlingstype.values().firstOrNull { it.name == name }
        }

        fun fromKode(kode: String): Behandlingstype? {
            return map[kode]
        }
    }
}