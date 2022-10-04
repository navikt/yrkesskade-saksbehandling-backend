package no.nav.yrkesskade.saksbehandling.hendelser

import no.nav.yrkesskade.saksbehandling.model.BrevutsendingUtfoertHendelse
import no.nav.yrkesskade.saksbehandling.service.BehandlingService
import no.nav.yrkesskade.saksbehandling.util.kallMetodeMedCallId
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class BrevutsendingUtfoertHendelseConsumer(private val behandlingService: BehandlingService) {

    @KafkaListener(
        id = "brevutsending-utfoert",
        topics = ["\${kafka.topic.brevutsending-utfoert.name}"],
        containerFactory = "brevutsendingUtfoertHendelseListenerContainerFactory",
        idIsGroup = false,
        autoStartup = "\${kafka.topic.brevutsending-utfoert.auto-startup:true}"
    )
    @Transactional
    fun listen(record: BrevutsendingUtfoertHendelse) {
        kallMetodeMedCallId(record.metadata.navCallId) {
            behandlingService.lagreUtgaaendeJournalpostFraBrevutsending(record.behandlingId, record.journalpostId)
        }
    }
}
