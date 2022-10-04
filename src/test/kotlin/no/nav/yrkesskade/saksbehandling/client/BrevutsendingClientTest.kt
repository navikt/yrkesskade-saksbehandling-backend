package no.nav.yrkesskade.saksbehandling.client

import no.nav.yrkesskade.saksbehandling.fixtures.brevutsendingBestiltHendelse
import no.nav.yrkesskade.saksbehandling.model.BrevutsendingBestiltHendelse
import no.nav.yrkesskade.saksbehandling.test.AbstractTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.TestPropertySourceUtils
import java.time.Instant
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@ContextConfiguration(initializers = [BrevutsendingClientTest.KafkaTopicInitializer::class])
internal class BrevutsendingClientTest : AbstractTest() {

    @Autowired
    lateinit var brevutsendingClient: BrevutsendingClient

    @Autowired
    private lateinit var brevutsendingConsumer: BrevutsendingConsumer

    @Test
    fun sendTilBrevutsending() {
        val brevutsendingBestiltHendelse = brevutsendingBestiltHendelse()

        assertDoesNotThrow {
            brevutsendingClient.sendTilBrevutsending(brevutsendingBestiltHendelse)
        }

        brevutsendingConsumer.getLatch().await(10000, TimeUnit.MILLISECONDS)

        Assertions.assertThat(brevutsendingConsumer.getPayload()).isEqualTo(brevutsendingBestiltHendelse)
    }

    class KafkaTopicInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(applicationContext: ConfigurableApplicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                applicationContext,

                "kafka.topic.brevutsending-bestilt=brevutsending-bestilt-topic-${System.currentTimeMillis()}"
            )
        }
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
