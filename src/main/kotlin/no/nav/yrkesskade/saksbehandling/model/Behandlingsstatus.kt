package no.nav.yrkesskade.saksbehandling.model

enum class Behandlingsstatus(val kode: String) {
    FERDIG("ferdig"),
    IKKE_PAABEGYNT("ikkePaabegynt"),
    UNDER_BEHANDLING("underBehandling"),
    OVERFOERT_LEGACY("overfoertLegacy");

    companion object {
        private val map = Behandlingsstatus.values().associateBy(Behandlingsstatus::kode)
        inline fun valueOfOrNull(name: String): Behandlingsstatus? {
            return values().firstOrNull { it.name == name }
        }

        fun fromKode(kode: String): Behandlingsstatus? {
            return map[kode]
        }
    }
}