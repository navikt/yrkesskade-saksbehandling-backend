package no.nav.yrkesskade.saksbehandling.util.kodeverk

import no.nav.yrkesskade.saksbehandling.model.Behandlingsstatus
import no.nav.yrkesskade.saksbehandling.model.Behandlingstype
import no.nav.yrkesskade.saksbehandling.model.Framdriftsstatus

private const val TYPE_BEHANDLINGSTYPE = "behandlingstype"
private const val TYPE_BEHANDLINGSSTATUS = "behandlingsstatus"
private const val TYPE_FRAMDRIFTSSTATUS = "framdriftsstatus"

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
        return kodeverkHolder.mapKodeTilVerdi(kode, TYPE_BEHANDLINGSTYPE)
    }

    fun mapBehandlingsstatus(status: Behandlingsstatus): String {
        val kode = when (status) {
            Behandlingsstatus.IKKE_PAABEGYNT -> "ikkePaabegynt"
            Behandlingsstatus.UNDER_BEHANDLING -> "underBehandling"
            Behandlingsstatus.FERDIG -> "ferdig"
        }
        return kodeverkHolder.mapKodeTilVerdi(kode, TYPE_BEHANDLINGSSTATUS)
    }

    fun mapFramdriftsstatus(framdriftsstatus: Framdriftsstatus): String {
        val kode = when (framdriftsstatus) {
            Framdriftsstatus.IKKE_PAABEGYNT -> "ikkePaabegynt"
            Framdriftsstatus.UNDER_ARBEID -> "underArbeid"
            Framdriftsstatus.PAA_VENT -> "paaVent"
            Framdriftsstatus.AVVENTER_SVAR -> "avventerSvar"
        }
        return kodeverkHolder.mapKodeTilVerdi(kode, TYPE_FRAMDRIFTSSTATUS)
    }

}
