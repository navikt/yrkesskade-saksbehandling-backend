package no.nav.yrkesskade.saksbehandling.graphql.client.saf

import com.expediagroup.graphql.generated.enums.*
import com.expediagroup.graphql.generated.journalpost.Bruker
import com.expediagroup.graphql.generated.journalpost.DokumentInfo
import com.expediagroup.graphql.generated.journalpost.Journalpost
import com.github.javafaker.Faker

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit

class JournalpostFactory {

    companion object {
        private val faker = Faker()

        fun enJournalpost(): Journalpost {
            return Journalpost(
                journalpostId = enJournalpostId(),
                journalstatus = enJournalstatus(),
                journalposttype = enJournalposttype(),
                tema = Tema.YRK,
                kanal = enKanal(),
                bruker = enBruker(),
                journalfoerendeEnhet = enJournalfoerendeEnhet(),
                behandlingstema = null,
                dokumenter = noenDokumenter(),
                datoOpprettet = LocalDateTime.ofInstant(ettTidspunkt(), ZoneOffset.UTC)
            )
        }

        fun enJournalpostId() = faker.number().numberBetween(0, 1000).toString()
        fun enJournalstatus() = faker.options().nextElement(Journalstatus.values())
        fun enJournalposttype() = faker.options().nextElement(Journalposttype.values())
        fun enKanal() = faker.options().nextElement(Kanal.values())
        fun enJournalfoerendeEnhet() = faker.regexify("[0-9]{4}")
        fun ettTidspunkt() =  faker.date().past(50, TimeUnit.DAYS).toInstant()
        fun enBruker(): Bruker {
            return Bruker(
                id = enBrukerId(),
                type = enBrukerIdType()
            )
        }
        fun enBrukerId() = faker.regexify("[0-9]{11}")
        fun enBrukerIdType() = faker.options().nextElement(BrukerIdType.values())

        fun noenDokumenter(): List<DokumentInfo> {
            val dokumenter = mutableListOf<DokumentInfo>()
            repeat(faker.number().numberBetween(0, 3)) {
                dokumenter.add(ettDokument())
            }
            return dokumenter
        }

        fun ettDokument(): DokumentInfo {
            return DokumentInfo(
                dokumentInfoId = enDokumentInfoId(),
                tittel = enDokumenttittel(),
                brevkode = enBrevkode()
            )
        }

        fun enDokumentInfoId() = faker.regexify("[0-9]{5}")
        fun enDokumenttittel() = faker.chuckNorris().fact()
        fun enBrevkode() = faker.regexify("NAV 13-[0-9]{2}\\.[0-9]{2}")

        fun Journalpost.medJournalpostId(journalpostId: String) = this.copy(journalpostId = journalpostId)
        fun Journalpost.medJournalposttype(journalposttype: Journalposttype) = this.copy(journalposttype = journalposttype)
        fun Journalpost.medDokumenter(dokumenter: List<DokumentInfo>) = this.copy(dokumenter = dokumenter)
    }
}