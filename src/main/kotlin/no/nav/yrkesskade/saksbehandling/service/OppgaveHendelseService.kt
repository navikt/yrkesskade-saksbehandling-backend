package no.nav.yrkesskade.saksbehandling.service

import no.nav.yrkesskade.saksbehandling.hendelser.oppgave.model.OppgaveRecord
import no.nav.yrkesskade.saksbehandling.model.*
import no.nav.yrkesskade.saksbehandling.repository.BehandlingRepository
import no.nav.yrkesskade.saksbehandling.repository.DokumentMetaRepository
import no.nav.yrkesskade.saksbehandling.repository.SakRepository
import no.nav.yrkesskade.saksbehandling.util.getSecureLogger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.ZoneOffset

@Service
class OppgaveHendelseService(
    private val sakRepository: SakRepository,
    private val behandlingRepository: BehandlingRepository,
    private val dokumentMetaRepository: DokumentMetaRepository
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val log = LoggerFactory.getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    @Transactional
    fun prosesserOppgaveOpprettetHendelse(oppgaveRecord: OppgaveRecord) {
        log.info("Prosesser oppgave med oppgave id: ${oppgaveRecord.id}")

        if (!oppgaveRecord.behandlesAvApplikasjon.isNullOrBlank()) {
            log.info("Oppgave ${oppgaveRecord.id} behandles av ${oppgaveRecord.behandlesAvApplikasjon} - avslutter videre prosessering")
            return
        }

        // finn siste åpne sak - dersom flere eksisterer - må denne prosesseres manuelt
        var aapneSaker = sakRepository.findByBrukerIdentifikator(oppgaveRecord.ident.folkeregisterident)
        if (!aapneSaker.isNullOrEmpty() && aapneSaker.size > 1) {
            log.warn("Flere åpne saker funnet for ${oppgaveRecord.ident.folkeregisterident} - Manuell prosessering påkrevd")
            return
        }

        var sak: SakEntity = finnEllerOpprettSak(aapneSaker, oppgaveRecord)
        var behandling: BehandlingEntity = finnEllerOpprettBehandling(sak, oppgaveRecord)
        opprettDokument(behandling, oppgaveRecord)

    }

    private fun opprettDokument(behandling: BehandlingEntity, oppgaveRecord: OppgaveRecord): DokumentMetaEntity {
        // hent dokumenter for oppgave fra SAF med journalpostId
        log.info("Oppretter dokument for behandling ${behandling.behandlingId}")
        val dokumentMeta = DokumentMetaEntity(
            journalpostId = oppgaveRecord.journalpostId.orEmpty(),
            dokumenttype = Dokumenttype.INNKOMMET,
            dokumentId = -1, // autogenerert
            avsenderIdentifikator = "TEST", // hentes fra dokument?
            avsenderrolle = "TEST",// hentes fra dokument?
            dokumentkategori = Dokumentkategori.SKADEMELDING, //?
            dokumentnavn = "TEST", // hentes fra dokument,
            opprettetTidspunkt = Instant.now(), // hentes fra dokument
            filer = emptyList(),
            behandling = behandling!!
        )

        return dokumentMetaRepository.save(dokumentMeta)
    }

    private fun finnEllerOpprettBehandling(sak: SakEntity, oppgaveRecord: OppgaveRecord): BehandlingEntity {
        // sjekk oppgave - legges til eksisterende behandling
        return if (sak.behandlinger.isNullOrEmpty()) {
            log.info("Ingen behandlinger eksisterer i sak ${sak.sakId} - oppretter behandling")
            // opprett behandling
            val behandling = BehandlingEntity(
                behandlingId = -1, // autogenerert
                sak = sak,
                ansvarligEnhet = oppgaveRecord.tildeltEnhetsnr,
                behandlingsAnsvarligIdent = null,
                dokumentMetaer = emptyList(),
                status = Behandlingsstatus.IKKE_PAABEGYNT,
                statuskategori = oppgaveRecord.statuskategori,
                oppgaveId = oppgaveRecord.id.toString(),
                behandlingResultater = emptyList(),
                opprettetTidspunkt = oppgaveRecord.opprettetTidspunkt.toInstant(ZoneOffset.UTC),
                opprettetAv = oppgaveRecord.opprettetAv,
                oppgavetype = oppgaveRecord.oppgavetype,
                aktivDato = oppgaveRecord.aktivDato,
                fristFerdigstillelse = oppgaveRecord.fristFerdigstillelse,
                behandlingstema = oppgaveRecord.behandlingstema,
                endretAv = oppgaveRecord.endretAv
            )
            behandlingRepository.save(behandling)
        } else {
            // for test -- hent første behandling
            val behandling = sak.behandlinger.first()
            log.info("Behandling eksisterer i sak ${sak.sakId} - behandling id: ${behandling.behandlingId} oppgave id: ${behandling.oppgaveId}")
            return behandling
        }
    }

    private fun finnEllerOpprettSak(aapneSaker: List<SakEntity>?, oppgaveRecord: OppgaveRecord): SakEntity {

        return if (aapneSaker.isNullOrEmpty()) {
            // opprett ny sak for bruker
            log.info("Sak finnes ikke for ${oppgaveRecord.ident.folkeregisterident} - sak opprettes")
            val sak = SakEntity(
                brukerEtternavn = "TEST",
                brukerMellomnavn = "TEST",
                brukerFornavn = "TEST",
                brukerIdentifikator = oppgaveRecord.ident.folkeregisterident,
                opprettetAv = "SYSTEM",
                opprettetTidspunkt = Instant.now(),
                sakstatus = Sakstatus.AAPEN,
                sakstype = Sakstype.YRKESSKADE, // TEST
                aktoerId = oppgaveRecord.ident.verdi,
                behandlinger = emptyList(),
                sakId = -1 // autogenerert
            )
            return sakRepository.save(sak)
        } else {
            val sak = aapneSaker.first()
            log.info("Sak finnes for ${oppgaveRecord.ident.folkeregisterident} - Saksnummer ${sak.sakId}")
            return sak
        }
    }
}