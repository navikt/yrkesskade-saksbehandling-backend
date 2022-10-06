package no.nav.yrkesskade.saksbehandling.service

import no.nav.yrkesskade.saksbehandling.client.BrevutsendingClient
import no.nav.yrkesskade.saksbehandling.client.JsonToPdfClient
import no.nav.yrkesskade.saksbehandling.model.Brev
import no.nav.yrkesskade.saksbehandling.model.BrevutsendingBestiltHendelse
import no.nav.yrkesskade.saksbehandling.model.BrevutsendingMetadata
import no.nav.yrkesskade.saksbehandling.model.Mottaker
import no.nav.yrkesskade.saksbehandling.util.MDCConstants
import no.nav.yrkesskade.saksbehandling.util.kodeverk.KodeverdiMapper
import no.nav.yrkesskade.saksbehandling.util.kodeverk.KodeverkHolder
import org.slf4j.MDC
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.Base64

@Service
class BrevService(
    private val brevutsendingClient: BrevutsendingClient,
    private val jsonToPdfClient: JsonToPdfClient,
    private val behandlingService: BehandlingService,
    kodeverkService: KodeverkService
) {

    private val kodeverdiMapper: KodeverdiMapper = KodeverdiMapper(KodeverkHolder.init(kodeverkService = kodeverkService))

    fun sendTilBrevutsending(behandlingId: Long, brev: Brev) {
        val behandling = behandlingService.hentBehandling(behandlingId)
        val tittel = kodeverdiMapper.mapDokumentkategori(brev.dokumentkategori)

        brevutsendingClient.sendTilBrevutsending(
            BrevutsendingBestiltHendelse(
                behandlingId = behandlingId,
                brevinnhold = brev.innhold,
                tittel = tittel,
                mottaker = Mottaker(foedselsnummer = behandling.brukerId),
                metadata = BrevutsendingMetadata(
                    tidspunktBestilt = Instant.now(),
                    navCallId = MDC.get(MDCConstants.MDC_CALL_ID)
                )
            )
        )
    }

    /**
     * Generer en PDF basert på brev innhold og returnerer data som Base64
     */
    fun genererBrev(brev: Brev): String {
        val data = jsonToPdfClient.genererPdfFraJson(brev.innhold)
        return Base64.getEncoder().encodeToString(data)
    }
}