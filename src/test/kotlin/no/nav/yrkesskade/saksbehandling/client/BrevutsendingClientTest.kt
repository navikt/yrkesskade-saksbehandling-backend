package no.nav.yrkesskade.saksbehandling.client

import no.nav.yrkesskade.saksbehandling.fixtures.brevutsendingBestiltHendelse
import no.nav.yrkesskade.saksbehandling.model.BrevutsendingBestiltHendelse
import no.nav.yrkesskade.saksbehandling.test.AbstractTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.timeout

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

internal class BrevutsendingClientTest : AbstractTest() {

    @Autowired
    lateinit var brevutsendingClient: BrevutsendingClient

    @Autowired
    private lateinit var brevutsendingConsumer: BrevutsendingConsumer

    @Test
    fun sendTilBrevutsending() {
        val brevutsendingBestiltHendelse = brevutsendingBestiltHendelse()
        brevutsendingClient.sendTilBrevutsending(brevutsendingBestiltHendelse)

        brevutsendingConsumer.getLatch().await(10000, TimeUnit.MILLISECONDS)

        Assertions.assertThat(brevutsendingConsumer.getPayload()).isEqualTo(brevutsendingBestiltHendelse)
    }
}

@Component
class BrevutsendingConsumer {

    private lateinit var payload: BrevutsendingBestiltHendelse
    private val latch = CountDownLatch(1)

    @KafkaListener(topics = ["\${kafka.topic.brevutsending-bestilt}"])
    fun receive(record: BrevutsendingBestiltHendelse) {
        payload = record
        latch.countDown()
    }

    fun getPayload() = payload
    fun getLatch() = latch
}