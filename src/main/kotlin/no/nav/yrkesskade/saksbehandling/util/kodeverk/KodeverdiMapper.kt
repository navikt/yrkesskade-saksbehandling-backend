package no.nav.yrkesskade.saksbehandling.util.kodeverk

import no.nav.yrkesskade.saksbehandling.model.Behandlingsstatus
import no.nav.yrkesskade.saksbehandling.model.Behandlingstype
import no.nav.yrkesskade.saksbehandling.model.Framdriftsstatus

private const val TYPE_BEHANDLINGSTYPE = "behandlingstype"
private const val TYPE_BEHANDLINGSSTATUS = "behandlingsstatus"
private const val TYPE_FRAMDRIFTSSTATUS = "framdriftsstatus"
private const val TYPE_DOKUMENTKATEGORI = "dokumenttype"

class KodeverdiMapper(val kodeverkHolder: KodeverkHolder) {

    fun mapBehandlingstype(behandlingstype: Behandlingstype): String {
       return kodeverkHolder.mapKodeTilVerdi(behandlingstype.kode, TYPE_BEHANDLINGSTYPE)
    }

    fun mapBehandlingsstatus(status: Behandlingsstatus): String {
        return kodeverkHolder.mapKodeTilVerdi(status.kode, TYPE_BEHANDLINGSSTATUS)
    }

    fun mapFramdriftsstatus(framdriftsstatus: Framdriftsstatus): String {
        return kodeverkHolder.mapKodeTilVerdi(framdriftsstatus.kode, TYPE_FRAMDRIFTSSTATUS)
    }

    fun mapDokumentkategori(dokumentkategori: String): String {
        return kodeverkHolder.mapKodeTilVerdi(dokumentkategori, TYPE_DOKUMENTKATEGORI)
    }

}
