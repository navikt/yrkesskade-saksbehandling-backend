package no.nav.yrkesskade.saksbehandling.fixtures

import no.nav.yrkesskade.saksbehandling.model.Brev
import no.nav.yrkesskade.saksbehandling.model.BrevutsendingBestiltHendelse
import no.nav.yrkesskade.saksbehandling.model.BrevutsendingMetadata
import no.nav.yrkesskade.saksbehandling.model.pdf.PdfData
import no.nav.yrkesskade.saksbehandling.model.pdf.PdfTemplate
import java.time.Instant
import java.util.UUID

fun brevutsendingBestiltHendelse(): BrevutsendingBestiltHendelse {
    return BrevutsendingBestiltHendelse(
        Brev(
            tittel = "tittel",
            brevkode = "NAV",
            enhet = "9999",
            template = PdfTemplate.TANNLEGEERKLAERING_VEILEDNING,
            innhold = PdfData(
                brevtype = "brevtype",
                uuid = UUID.randomUUID().toString()
            )
        ),
        metadata = BrevutsendingMetadata(
            innkommendeJournalpostId = "12345678",
            tidspunktBestilt = Instant.now(),
            navCallId = UUID.randomUUID().toString()
        )
    )
}