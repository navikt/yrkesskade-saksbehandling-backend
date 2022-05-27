package no.nav.yrkesskade.saksbehandling.hendelser.oppgave

import no.nav.yrkesskade.saksbehandling.hendelser.oppgave.model.OppgaveRecord
import no.nav.yrkesskade.saksbehandling.service.OppgaveHendelseService
import no.nav.yrkesskade.saksbehandling.util.kallMetodeMedCallId
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service
import java.util.concurrent.CountDownLatch
import javax.transaction.Transactional


@Service
class OppgaveHendelser(
    private val oppgaveHendelseService: OppgaveHendelseService
) {

    val latch = CountDownLatch(1) // single-thread safety - bra for test
    var payload: OppgaveRecord? = null

    @KafkaListener(
        id = "yrkesskade-melding-mottak",
        topics = ["\${kafka.topic.aapen-oppgave-opprettet}"],
        containerFactory = "oppgaveOpprettetHendelseListenerContainerFactory",
        idIsGroup = false,
        properties = [
            "spring.json.value.default.type=no.nav.yrkesskade.saksbehandling.hendelser.oppgave.model.OppgaveRecord"
        ]
    )
    @Transactional
    fun listen(@Payload record: OppgaveRecord) {
        kallMetodeMedCallId {
            oppgaveHendelseService.prosesserOppgaveOpprettetHendelse(record)
            payload = record
            latch.countDown()
        }
    }
}