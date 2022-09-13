package no.nav.yrkesskade.saksbehandling.fixtures.journalpost

import com.expediagroup.graphql.generated.Journalpost
import com.expediagroup.graphql.generated.enums.*
import com.expediagroup.graphql.generated.journalpost.Bruker
import com.expediagroup.graphql.generated.journalpost.DokumentInfo
import java.time.LocalDateTime

fun gyldigJournalpostTannlegeerklaeringMedAktoerId(): com.expediagroup.graphql.generated.journalpost.Journalpost {
    return com.expediagroup.graphql.generated.journalpost.Journalpost(
        journalpostId = "1337",
        journalstatus = Journalstatus.MOTTATT,
        journalposttype = Journalposttype.I,
        tema = Tema.YRK,
        kanal = Kanal.SKAN_IM,
        bruker = Bruker("2751737180290", BrukerIdType.AKTOERID),
        journalfoerendeEnhet = "4849",
        behandlingstema = null,
        dokumenter = listOf(
            DokumentInfo(
                "dokument-test-id",
                "Tannlegeerklæring ved yrkesskade",
                "NAV 13-00.08"
            )
        ),
        datoOpprettet = LocalDateTime.of(2022, 2, 2, 2, 2, 2, 2)
    )
}

fun journalpostResultTannlegeerklaeringWithBrukerAktoerid(): Journalpost.Result {
    return Journalpost.Result(gyldigJournalpostTannlegeerklaeringMedAktoerId())
}

fun dokumentInfoListeMedTannlegeerklaering(): List<DokumentInfo> {
    val dokInfo1 = DokumentInfo("dok1", "Et vilkårlig dokument", "NAV000-1")
    val dokInfo2 = DokumentInfo("dok2", "Et annet dokument", "NAV000-2")
    val dokInfoTannlegeerklaering = DokumentInfo("dok3", "Tannlegeerklæring ved yrkesskade", "NAV 13-00.08")

    return listOf(dokInfo1, dokInfo2, dokInfoTannlegeerklaering)
}
