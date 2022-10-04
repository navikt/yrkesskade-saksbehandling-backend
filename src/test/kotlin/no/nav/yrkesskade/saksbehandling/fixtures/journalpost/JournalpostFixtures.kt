package no.nav.yrkesskade.saksbehandling.fixtures.journalpost

import com.expediagroup.graphql.generated.Journalpost
import com.expediagroup.graphql.generated.enums.*
import com.expediagroup.graphql.generated.journalpost.Bruker
import com.expediagroup.graphql.generated.journalpost.DokumentInfo
import java.time.LocalDateTime

fun gyldigJournalpostMedAktoerId(): com.expediagroup.graphql.generated.journalpost.Journalpost {
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
                "Melding om yrkesskade eller yrkessykdom som er påført under tjeneste på skip eller under fiske/fangst",
                "NAV 13-07.08"
            )
        ),
        datoOpprettet = LocalDateTime.of(2022, 1, 1, 1, 1, 1, 1)
    )
}

fun gyldigJournalpostMedAktoerIdUtenDokumenter(): com.expediagroup.graphql.generated.journalpost.Journalpost {
    return com.expediagroup.graphql.generated.journalpost.Journalpost(
        journalpostId = "1337",
        journalstatus = Journalstatus.MOTTATT,
        journalposttype = Journalposttype.I,
        tema = Tema.YRK,
        kanal = Kanal.SKAN_IM,
        bruker = Bruker("2751737180290", BrukerIdType.AKTOERID),
        journalfoerendeEnhet = "4849",
        behandlingstema = null,
        dokumenter = emptyList(),
        datoOpprettet = LocalDateTime.of(2022, 1, 1, 1, 1, 1, 1)
    )
}

fun journalpostResultWithBrukerAktoerid(): Journalpost.Result {
    return Journalpost.Result(gyldigJournalpostMedAktoerId())
}