package no.nav.yrkesskade.saksbehandling.service

import no.nav.yrkesskade.kodeverk.model.KodeverdiDto
import no.nav.yrkesskade.saksbehandling.client.Kodeverkklient
import no.nav.yrkesskade.saksbehandling.model.dto.KodeverkTidData
import no.nav.yrkesskade.saksbehandling.model.dto.KodeverkTypeKategori
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class KodeverkService(
    private val kodeverkklient: Kodeverkklient,
    @Value("\${kodeverk.cache.gyldigTidMinutter}") val gyldigTidMinutter: Long = 60
) {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val log = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

    val kodeverkMap: MutableMap<KodeverkTypeKategori, KodeverkTidData> = mutableMapOf()


    fun hentKodeverk(type: String, kategori: String?, spraak: String = "nb"): Map<String, KodeverdiDto> {
        val key = KodeverkTypeKategori(type, kategori)

        if (!gyldig(type, kategori, spraak)) {
            val map = kodeverkklient.hentKodeverk(type, kategori, spraak)
            kodeverkMap[key] = KodeverkTidData(map)
            log.info("Hentet kodeverk for type=$type, (kategori=$kategori,) språk=$spraak. Antall koder=${kodeverkMap[key]?.data?.size}.")
        }

        return kodeverkMap[key]?.data ?: emptyMap()
    }


    private fun gyldig(type: String, kategori: String?, spraak: String): Boolean {
        val key = KodeverkTypeKategori(type, kategori)
        val kodeverkTidData: KodeverkTidData? = kodeverkMap[key]

        if (kodeverkTidData == null ||
            kodeverkTidData.hentetTid.isBefore(Instant.now().plus(-gyldigTidMinutter, ChronoUnit.MINUTES))
        ) {
            return false
        }
        return true
    }
}