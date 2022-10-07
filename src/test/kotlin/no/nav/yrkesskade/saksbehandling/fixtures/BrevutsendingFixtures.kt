package no.nav.yrkesskade.saksbehandling.fixtures

import no.nav.yrkesskade.saksbehandling.model.BrevutsendingBestiltHendelse
import no.nav.yrkesskade.saksbehandling.model.BrevutsendingMetadata
import no.nav.yrkesskade.saksbehandling.model.Mottaker
import no.nav.yrkesskade.saksbehandling.model.pdf.PdfInnholdElement
import no.nav.yrkesskade.saksbehandling.model.pdf.PdfTekstElement
import java.time.Instant
import java.util.UUID

fun brevutsendingBestiltHendelse(): BrevutsendingBestiltHendelse {
    return BrevutsendingBestiltHendelse(
        tittel = "tittel",
        brevinnhold = listOf(
            PdfInnholdElement(
                type = "paragraph",
                children = listOf(
                    PdfTekstElement(
                        text = "test"
                    )
                ),
                align = "left"
            ),
        ),
        enhet = "4849",
        behandlingId = 1234,
        mottaker = Mottaker(foedselsnummer = "012345678910"),
        metadata = BrevutsendingMetadata(
            tidspunktBestilt = Instant.now(),
            navCallId = UUID.randomUUID().toString()
        )
    )
}