package no.nav.yrkesskade.saksbehandling.service

import no.nav.yrkesskade.saksbehandling.client.BrevutsendingClient
import no.nav.yrkesskade.saksbehandling.client.JsonToPdfClient
import no.nav.yrkesskade.saksbehandling.client.dokgen.DokgenClient
import no.nav.yrkesskade.saksbehandling.model.Brev
import no.nav.yrkesskade.saksbehandling.model.BrevutsendingBestiltHendelse
import no.nav.yrkesskade.saksbehandling.model.BrevutsendingMetadata
import no.nav.yrkesskade.saksbehandling.util.MDCConstants
import org.slf4j.MDC
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.Base64

@Service
class BrevService(
    private val brevutsendingClient: BrevutsendingClient,
    private val dokgenClient: DokgenClient,
    private val jsonToPdfClient: JsonToPdfClient
    ) {

    fun sendTilBrevutsending(brev: Brev, journalpostId: String) {
        brevutsendingClient.sendTilBrevutsending(
            BrevutsendingBestiltHendelse(
                brev = brev,
                metadata = BrevutsendingMetadata(
                    innkommendeJournalpostId = journalpostId,
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
        val data = jsonToPdfClient.genererPdfFraJson(brev.innhold.innhold)
        return Base64.getEncoder().encodeToString(data)
    }
}