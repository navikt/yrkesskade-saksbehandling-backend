package no.nav.yrkesskade.saksbehandling.model

enum class Framdriftsstatus(val kode: String) {
    IKKE_PAABEGYNT("ikkePaabegynt"),
    UNDER_ARBEID("underArbeid"),
    PAA_VENT("paaVent"),
    AVVENTER_SVAR("avventerSvar");

    companion object {
        private val map = Framdriftsstatus.values().associateBy(Framdriftsstatus::kode)
        inline fun valueOfOrNull(name: String): Framdriftsstatus? {
            return Framdriftsstatus.values().firstOrNull { it.name == name }
        }

        fun fromKode(kode: String): Framdriftsstatus? {
            return map[kode]
        }
    }
}
