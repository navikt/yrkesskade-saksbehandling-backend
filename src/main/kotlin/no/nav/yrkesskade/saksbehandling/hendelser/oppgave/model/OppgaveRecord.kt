package no.nav.yrkesskade.saksbehandling.hendelser.oppgave.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDate
import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class OppgaveRecord(
    val id: Long,
    val ident: Ident,
    val tildeltEnhetsnr: String?,
    val journalpostId: String?,
    val temagruppe: String?,
    val tema: String,
    val behandlingstema: String,
    val oppgavetype: String,
    val fristFerdigstillelse: LocalDate,
    val aktivDato: LocalDate,
    val behandlesAvApplikasjon: String?,
    val mappeId: Long?,
    val endretAvEnhetsnr: String?,
    val opprettetAvEnhetsnr: String?,
    val opprettetTidspunkt: LocalDateTime,
    val opprettetAv: String,
    val endretAv: String?,
    val status: Oppgavestatus?,
    val statuskategori: Oppgavestatuskategori
)