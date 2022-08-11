package no.nav.yrkesskade.saksbehandling.hendelser

import no.nav.yrkesskade.saksbehandling.model.DokumentTilSaksbehandlingHendelse
import no.nav.yrkesskade.saksbehandling.service.Dokumentmottak
import no.nav.yrkesskade.saksbehandling.util.kallMetodeMedCallId
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class DokumentTilSaksbehandlingHendelseConsumer(private val dokumentmottak: Dokumentmottak) {

    @KafkaListener(
        id = "dokument-til-saksbehandling",
        topics = ["\${kafka.topic.dokument-til-saksbehandling}"],
        containerFactory = "dokumentTilSaksbehandlingHendelseListenerContainerFactory",
        idIsGroup = false
    )
    @Transactional(propagation = Propagation.REQUIRED)
    fun listen(record: DokumentTilSaksbehandlingHendelse) {
        kallMetodeMedCallId(record.metadata.callId) {
            dokumentmottak.mottaDokument(record)
        }
    }
}
