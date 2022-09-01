package no.nav.yrkesskade.saksbehandling.api.v1.brev

import io.swagger.v3.oas.annotations.Parameter
import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.yrkesskade.saksbehandling.api.v1.brev.dto.BrevDto
import no.nav.yrkesskade.saksbehandling.model.Brev
import no.nav.yrkesskade.saksbehandling.model.pdf.PdfData
import no.nav.yrkesskade.saksbehandling.security.ISSUER
import no.nav.yrkesskade.saksbehandling.service.BrevService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@ProtectedWithClaims(issuer = ISSUER)
@RestController
@RequestMapping(path = ["/api/v1/brev"], produces = [MediaType.APPLICATION_JSON_VALUE])
class BrevController(
    private val brevService: BrevService
) {

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun opprettBrev(@Parameter(description = "brev data som skal opprettes og distribueres") @RequestBody brev: BrevDto): ResponseEntity<Void>  {

        val pdfData = PdfData(
            brevtype = brev.brevtype,
            uuid = UUID.randomUUID().toString()
        )

        val brevTilBrevutsending = Brev(
            tittel = brev.tittel,
            brevkode = brev.brevkode,
            enhet = brev.enhet,
            template = brev.template,
            innhold = pdfData
        )

        brevService.sendTilBrevutsending(brevTilBrevutsending, brev.innkommendeJournalpostId)

        return ResponseEntity.accepted().build()
    }
}