package no.nav.yrkesskade.saksbehandling.model

enum class Behandlingsstatus {
    FERDIG,
    IKKE_PAABEGYNT,
    UNDER_BEHANDLING;

    companion object {
        inline fun valueOfOrNull(name: String): Behandlingsstatus? {
            return values().firstOrNull { it.name == name }
        }
    }
}