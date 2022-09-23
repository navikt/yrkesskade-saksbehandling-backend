package no.nav.yrkesskade.saksbehandling.client.oppgave

import java.time.LocalDate
import java.time.OffsetDateTime

data class OppgaveResponse(
    val antallTreffTotalt: Int,
    val oppgaver: List<Oppgave>
)

data class Oppgave(
    val id: Long,
    val tildeltEnhetsnr: String,
    val endretAvEnhetsnr: String? = null,
    val opprettetAvEnhetsnr: String? = null,
    val journalpostId: String? = null,
    val journalpostkilde: String? = null,
    val behandlesAvApplikasjon: String? = null,
    val saksreferanse: String? = null,
    val bnr: String? = null,
    val samhandlernr: String? = null,
    val aktoerId: String? = null,
    val identer: List<Ident>? = null,
    val orgnr: String? = null,
    val tilordnetRessurs: String? = null,
    val beskrivelse: String? = null,
    val temagruppe: String? = null,
    val tema: String,
    val behandlingstema: String? = null,
    val oppgavetype: String,
    val behandlingstype: String? = null,
    val versjon: Int,
    val mappeId: Long? = null,
    val opprettetAv: String,
    val endretAv: String? = null,
    val prioritet: Prioritet,
    val status: Status,
    val metadata: Map<String, String>? = null,
    val fristFerdigstillelse: LocalDate?,
    val aktivDato: LocalDate,
    val opprettetTidspunkt: OffsetDateTime,
    val ferdigstiltTidspunkt: OffsetDateTime? = null,
    val endretTidspunkt: OffsetDateTime? = null
)

// De feltene som skal kunne endres må gjøres mutable/defineres som var isf val
data class OpprettJournalfoeringOppgave(
    val beskrivelse: String? = null,
    val journalpostId: String? = null,
    val aktoerId: String? = null,
    val tema: String,
    val tildeltEnhetsnr: String? = null,
    val behandlingstema: String? = null,
    val oppgavetype: String,
    val behandlingstype: String? = null,
    val prioritet: Prioritet,
    val fristFerdigstillelse: LocalDate?,
    val aktivDato: LocalDate? = null
)

data class Ident(
    val ident: String? = null,
    val gruppe: Gruppe? = null
)

enum class Oppgavetype(val kortnavn: String) {
    JOURNALFOERING("JFR")
}

enum class Gruppe {
    FOLKEREGISTERIDENT, AKTOERID, NPID
}

enum class Prioritet {
    HOY, NORM, LAV
}

enum class Status {
    OPPRETTET, AAPNET, UNDER_BEHANDLING, FERDIGSTILT, FEILREGISTRERT;

    fun statuskategori(): Statuskategori {
        return when (this) {
            AAPNET, OPPRETTET, UNDER_BEHANDLING -> Statuskategori.AAPEN
            FEILREGISTRERT, FERDIGSTILT -> Statuskategori.AVSLUTTET
        }
    }
}

enum class Statuskategori {
    AAPEN, AVSLUTTET
}

/**
 * Enum for mapping fra enkelte brevkoder til behandlingstema og behandlingstype.
 * Behandlingstema og behandlingstype sendes til Oppgave-API for å sørge for at brevene under får satt riktig
 * "Gjelder-kode" i Gosys og rutes til riktig enhet.
 * Kodene hentes "egentlig" med oppslag mot felles kodeverk, men det er så få som gjelder YRK, så vi kan bare ha dem her.
 *
 * Beskrivelse av brevkoder:
 * UTL:             Brev - utland
 *
 * NAVe 13-13.05:   Ettersendelse til søknad fra selvstendig næringsdrivende og frilansere om opptak i
 *                  frivillig trygd med rett til særytelser ved yrkesskade
 *
 * NAVe 13-07.05:   Ettersendelse til melding om yrkesskade eller yrkessykdom som er
 *                  påført under arbeid på norsk eller utenlandsk landterritorium
 *
 * NAV 13-13.05:    Søknad fra selvstendig næringsdrivende og frilansere om opptak i frivillig trygd
 *                  med rett til særytelser ved yrkesskade
 */
enum class KrutkodeMapping(val brevkode: String?, val behandlingstema: String?, val behandlingstype: String?) {
    UTL("UTL", "ab0276", "ae0106"),
    NAVe_13_13_05("NAVe 13-13.05", "ab0085", null),
    NAVe_13_07_05("NAVe 13-07.05", "ab0276", null),
    NAV_13_13_05("NAV 13-13.05", "ab0085", null),
    UKJENT(null, null, null);

    companion object {
        fun fromBrevkode(brevkode: String?) = values().find { it.brevkode == brevkode?.trim() } ?: UKJENT
    }
}


