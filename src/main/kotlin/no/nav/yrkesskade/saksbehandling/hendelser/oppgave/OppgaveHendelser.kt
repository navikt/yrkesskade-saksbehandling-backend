package no.nav.yrkesskade.saksbehandling.hendelser.oppgave

import no.nav.yrkesskade.saksbehandling.config.FeatureToggleConfig
import no.nav.yrkesskade.saksbehandling.config.FeatureToggles
import no.nav.yrkesskade.saksbehandling.hendelser.oppgave.model.OppgaveRecord
import no.nav.yrkesskade.saksbehandling.service.OppgaveHendelseService
import no.nav.yrkesskade.saksbehandling.util.kallMetodeMedCallId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.event.EventListener
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.config.KafkaListenerEndpointRegistry
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service
import java.util.concurrent.CountDownLatch
import javax.transaction.Transactional


@Service
class OppgaveHendelser(
    private val kafkaListenerEndpointRegistry: KafkaListenerEndpointRegistry,
    private val oppgaveHendelseService: OppgaveHendelseService,
    private val featureToggleConfig: FeatureToggleConfig
) {
    val latch = CountDownLatch(1) // single-thread safety - bra for test
    var payload: OppgaveRecord? = null

    @KafkaListener(
        id = "yrkesskade-saksbehandling-backend-oppgave",
        topics = ["\${kafka.topic.aapen-oppgave-opprettet}"],
        containerFactory = "oppgaveOpprettetHendelseListenerContainerFactory",
        idIsGroup = false,
        properties = [
            "spring.json.value.default.type=no.nav.yrkesskade.saksbehandling.hendelser.oppgave.model.OppgaveRecord"
        ],
        autoStartup = "false" // lytteren startes opp etter en sjekk på unleash killswitch
    )
    @Transactional
    fun listen(@Payload record: OppgaveRecord) {
        payload = record
        kallMetodeMedCallId {
            oppgaveHendelseService.prosesserOppgaveOpprettetHendelse(record)
            latch.countDown()
        }
    }

    @EventListener
    fun onStartet(event: ApplicationStartedEvent) {
        if (featureToggleConfig.featureToggle().isEnabled(FeatureToggles.OPPGAVE_HENDELSER.toggleId, false)) {
            kafkaListenerEndpointRegistry.getListenerContainer("yrkesskade-saksbehandling-backend-oppgave")?.apply {
                start()
            }
        }
    }
}