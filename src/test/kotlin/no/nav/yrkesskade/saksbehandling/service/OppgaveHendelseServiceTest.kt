package no.nav.yrkesskade.saksbehandling.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.yrkesskade.saksbehandling.hendelser.oppgave.model.OppgaveRecord
import no.nav.yrkesskade.saksbehandling.repository.BehandlingRepository
import no.nav.yrkesskade.saksbehandling.repository.SakRepository
import no.nav.yrkesskade.saksbehandling.test.AbstractTest
import oppgaveMedBehandlesAvApplikasjon
import oppgaveUtenBehandlesAvApplikasjon
import oppgaveUtenBehandlesAvApplikasjonAlt2
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

@Transactional
class OppgaveHendelseServiceTest : AbstractTest() {

    @Autowired
    lateinit var oppgaveHendelseService: OppgaveHendelseService

    @Autowired
    lateinit var behandlingRepository: BehandlingRepository

    @Autowired
    lateinit var sakRepository: SakRepository

    val objectMapper: ObjectMapper

    init {
        objectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())
    }

    @Test
    fun `prosesser oppgave med behandles av applikasjon satt`() {
        val oppgaveRecord = objectMapper.readValue(oppgaveMedBehandlesAvApplikasjon(), OppgaveRecord::class.java)

        oppgaveHendelseService.prosesserOppgaveOpprettetHendelse(oppgaveRecord)

        val behandling = behandlingRepository.findByOppgaveId("99")
        assertThat(behandling).isNull()
    }

    @Test
    fun `prosesser oppgave uten behandles av applikasjon satt`() {
        val brukerIdent = "12345678910"

        val oppgaveRecord = objectMapper.readValue(oppgaveUtenBehandlesAvApplikasjon(), OppgaveRecord::class.java)

        oppgaveHendelseService.prosesserOppgaveOpprettetHendelse(oppgaveRecord)

        var sak = sakRepository.findByBrukerIdentifikator(brukerIdent).first()
        val behandlinger = behandlingRepository.findBySak(sak)
        assertThat(behandlinger.size).isEqualTo(1)
    }

    @Test
    fun `prosesser to oppgaver med samme bruker ident på åpen sak`() {
        val brukerIdent = "12345678910"
        // ingen saker opprettet
        assertThat(sakRepository.findByBrukerIdentifikator(brukerIdent).size).isEqualTo(0)

        // oppgave 1
        val oppgaveRecordOppgave1 = objectMapper.readValue(oppgaveUtenBehandlesAvApplikasjon(), OppgaveRecord::class.java)
        oppgaveHendelseService.prosesserOppgaveOpprettetHendelse(oppgaveRecordOppgave1)

        // assert sak opprettet
        var saker = sakRepository.findByBrukerIdentifikator(brukerIdent)
        assertThat(saker.size).isEqualTo(1)
        var behandlinger = behandlingRepository.findBySak(saker.first())
        assertThat(behandlinger.size).isEqualTo(1)

        // oppgave 2
        val oppgaveRecordOppgave2 = objectMapper.readValue(oppgaveUtenBehandlesAvApplikasjonAlt2(), OppgaveRecord::class.java)
        oppgaveHendelseService.prosesserOppgaveOpprettetHendelse(oppgaveRecordOppgave2)

        // assert sak ikke opprettet
        saker = sakRepository.findByBrukerIdentifikator(brukerIdent)
        assertThat(saker.size).isEqualTo(1)
        behandlinger = behandlingRepository.findBySak(saker.first())
        assertThat(behandlinger.size).isEqualTo(2)
    }
}