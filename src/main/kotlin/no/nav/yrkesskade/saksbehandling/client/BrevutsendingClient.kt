package no.nav.yrkesskade.saksbehandling.client

import no.nav.yrkesskade.saksbehandling.model.BrevutsendingBestiltHendelse
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Component
import org.springframework.util.concurrent.ListenableFuture

@Component
class BrevutsendingClient(
    @Value("\${kafka.topic.brevutsending-bestilt}") private val topic: String,
    private val kafkaTemplate: KafkaTemplate<String, BrevutsendingBestiltHendelse>
) {

    fun sendTilBrevutsending(
        brevutsendingBestiltHendelse: BrevutsendingBestiltHendelse
    ): BrevutsendingBestiltHendelse {
        val future: ListenableFuture<SendResult<String, BrevutsendingBestiltHendelse>> =
            kafkaTemplate.send(topic, brevutsendingBestiltHendelse)
        val resultat = future.get()
        return resultat.producerRecord.value()
    }
}