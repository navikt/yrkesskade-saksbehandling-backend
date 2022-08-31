package no.nav.yrkesskade.saksbehandling.util.kodeverk

import no.nav.yrkesskade.kodeverk.model.KodeverdiDto
import no.nav.yrkesskade.saksbehandling.service.KodeverkService

class KodeverkHolder private constructor(private val kodeverkService: KodeverkService) {

    private val kodeverk: MutableMap<String, Map<String, KodeverdiDto>> = mutableMapOf()

    fun mapKodeTilVerdi(kode: String, kodeliste: String): String {
        return kodeverk[kodeliste]?.getOrDefault(kode, KodeverdiDto(kode, "Ukjent $kode"))?.verdi ?: "Ukjent kodeliste $kodeliste"
    }

    fun hentKodeverk(kategorinavn: String?) {
        // kodeverk uten kategorier
        listOf("behandlingstype").forEach {
            kodeverk[it] = kodeverkService.hentKodeverk(it, null, "nb")
        }

        if (kategorinavn != null) {
            // kodeverk med kategorier
            listOf("dummytype").forEach {
                kodeverk[it] = kodeverkService.hentKodeverk(it, kategorinavn, "nb")
            }
        }
    }

    companion object {
        fun init (kategorinavn: String? = null, kodeverkService: KodeverkService): KodeverkHolder {
            return KodeverkHolder(kodeverkService).apply {
                hentKodeverk(kategorinavn)
            }
        }
    }
}