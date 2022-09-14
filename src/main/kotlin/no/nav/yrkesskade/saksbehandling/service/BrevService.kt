package no.nav.yrkesskade.saksbehandling.service

import com.expediagroup.graphql.generated.HentIdenter
import com.expediagroup.graphql.generated.enums.BrukerIdType
import com.expediagroup.graphql.generated.enums.IdentGruppe
import no.nav.yrkesskade.saksbehandling.client.BrevutsendingClient
import no.nav.yrkesskade.saksbehandling.client.JsonToPdfClient
import no.nav.yrkesskade.saksbehandling.graphql.client.pdl.PdlClient
import no.nav.yrkesskade.saksbehandling.graphql.client.pdl.PdlException
import no.nav.yrkesskade.saksbehandling.model.BehandlingEntity
import no.nav.yrkesskade.saksbehandling.model.Brev
import no.nav.yrkesskade.saksbehandling.model.BrevutsendingBestiltHendelse
import no.nav.yrkesskade.saksbehandling.model.BrevutsendingMetadata
import no.nav.yrkesskade.saksbehandling.model.Mottaker
import no.nav.yrkesskade.saksbehandling.util.MDCConstants
import org.slf4j.MDC
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.Base64

@Service
class BrevService(
    private val brevutsendingClient: BrevutsendingClient,
    private val jsonToPdfClient: JsonToPdfClient,
    private val pdlClient: PdlClient,
    private val behandlingService: BehandlingService
    ) {

    private fun hentFoedselsnummer(behandling: BehandlingEntity): String {
        return when (behandling.brukerIdType) {
            BrukerIdType.FNR -> behandling.brukerId
            BrukerIdType.AKTOERID -> hentFoedselsnummerFraPdl(behandling.brukerId)
            else -> throw RuntimeException("Utsending av brev for brukerIdType ${behandling.brukerIdType} støttes ikke")
        }
    }

    private fun hentFoedselsnummerFraPdl(aktoerId: String): String {
        val hentIdenter: HentIdenter.Result? = pdlClient.hentIdenter(aktoerId, listOf(IdentGruppe.FOLKEREGISTERIDENT), false)
        return extractFoedselsnummer(hentIdenter)
    }

    private fun extractFoedselsnummer(identerResult: HentIdenter.Result?): String {
        return identerResult?.hentIdenter?.identer?.first {
                identInfo -> identInfo.gruppe == IdentGruppe.FOLKEREGISTERIDENT
        }?.ident ?: throw PdlException("Fant ikke fødselsnummer i PDL")
    }

    fun sendTilBrevutsending(behandlingId: Long, brev: Brev) {
        val behandling = behandlingService.hentBehandling(behandlingId)
        val foedselsnummer = hentFoedselsnummer(behandling)

        brevutsendingClient.sendTilBrevutsending(
            BrevutsendingBestiltHendelse(
                brev = brev,
                mottaker = Mottaker(foedselsnummer = foedselsnummer),
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
        val data = jsonToPdfClient.genererPdfFraJson(brev.innhold.innhold)
        return Base64.getEncoder().encodeToString(data)
    }
}