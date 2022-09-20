package no.nav.yrkesskade.saksbehandling.fixtures

import no.nav.yrkesskade.saksbehandling.model.Brev
import no.nav.yrkesskade.saksbehandling.model.BrevutsendingBestiltHendelse
import no.nav.yrkesskade.saksbehandling.model.BrevutsendingMetadata
import no.nav.yrkesskade.saksbehandling.model.Mottaker
import no.nav.yrkesskade.saksbehandling.model.pdf.PdfData
import no.nav.yrkesskade.saksbehandling.model.pdf.PdfInnholdElement
import no.nav.yrkesskade.saksbehandling.model.pdf.PdfTekstElement
import no.nav.yrkesskade.saksbehandling.model.pdf.PdfTemplate
import java.time.Instant
import java.util.UUID

fun brevutsendingBestiltHendelse(): BrevutsendingBestiltHendelse {
    return BrevutsendingBestiltHendelse(
        Brev(
            tittel = "tittel",
            brevkode = "NAV",
            enhet = "9999",
            template = PdfTemplate.VEILEDNINGSBREV_TANNLEGEERKLAERING,
            innhold = PdfData(
                brevtype = "brevtype",
                uuid = UUID.randomUUID().toString(),
                innhold = listOf(PdfInnholdElement(
                    type="paragraph",
                    children = listOf(PdfTekstElement(
                        text = "test")
                    ),
                    align = "left"),
                )
            )
        ),
        mottaker = Mottaker(foedselsnummer = "012345678910"),
        metadata = BrevutsendingMetadata(
            tidspunktBestilt = Instant.now(),
            navCallId = UUID.randomUUID().toString()
        )
    )
}