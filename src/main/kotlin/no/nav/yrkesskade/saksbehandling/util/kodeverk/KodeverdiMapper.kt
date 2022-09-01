package no.nav.yrkesskade.saksbehandling.util.kodeverk

import no.nav.yrkesskade.saksbehandling.model.Behandlingstype

class KodeverdiMapper(val kodeverkHolder: KodeverkHolder) {

    fun mapBehandlingstype(behandlingstype: Behandlingstype): String {
        val kode = when (behandlingstype) {
            Behandlingstype.ANKE -> "anke"
            Behandlingstype.GJENOPPRETTING -> "gjenoppretting"
            Behandlingstype.INNSYN -> "innsyn"
            Behandlingstype.KLAGE -> "klage"
            Behandlingstype.KRAV_MELDING -> "krav-melding"
            Behandlingstype.JOURNALFOERING -> "journalfoering"
            Behandlingstype.REVURDERING -> "revurdering"
            Behandlingstype.TILBAKEKREVING -> "tilbakekreving"
            Behandlingstype.VEILEDNING -> "veiledning"
        }
        return kodeverkHolder.mapKodeTilVerdi(kode, "behandlingstype")
    }

}
