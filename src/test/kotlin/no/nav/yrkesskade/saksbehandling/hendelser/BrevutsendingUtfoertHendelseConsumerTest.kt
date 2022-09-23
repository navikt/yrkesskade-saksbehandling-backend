package no.nav.yrkesskade.saksbehandling.hendelser

import no.nav.yrkesskade.saksbehandling.config.KafkaTestConfig
import no.nav.yrkesskade.saksbehandling.fixtures.journalpost.journalpostResultWithBrukerAktoerid
import no.nav.yrkesskade.saksbehandling.graphql.client.saf.SafClient
import no.nav.yrkesskade.saksbehandling.model.BrevutsendingUtfoertHendelse
import no.nav.yrkesskade.saksbehandling.model.BrevutsendingUtfoertMetadata
import no.nav.yrkesskade.saksbehandling.model.DokumentTilSaksbehandling
import no.nav.yrkesskade.saksbehandling.model.DokumentTilSaksbehandlingHendelse
import no.nav.yrkesskade.saksbehandling.model.DokumentTilSaksbehandlingMetadata
import no.nav.yrkesskade.saksbehandling.service.BehandlingService
import no.nav.yrkesskade.saksbehandling.test.AbstractTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@Import(KafkaTestConfig::class)
class BrevutsendingUtfoertHendelseConsumerTest : AbstractTest() {

    @Value("\${kafka.topic.brevutsending-utfoert}")
    lateinit var topic: String

    @Autowired
    lateinit var consumer: BrevutsendingUtfoertHendelseConsumerForTest

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, BrevutsendingUtfoertHendelse>

    @Test
    fun listen() {
        val payload = BrevutsendingUtfoertHendelse(
            behandlingId = 1234,
            journalpostId = "5678",
            metadata = BrevutsendingUtfoertMetadata(
                navCallId = UUID.randomUUID().toString()
            )
        )

        kafkaTemplate.send(topic, payload)

        // vent på at oppgavene i consumer blir fullført før vi gjennomfører testene
        consumer.latch.await(20000, TimeUnit.MILLISECONDS)
        assertThat(consumer.payload).isEqualTo(payload)
    }
}

@Primary
@Component
class BrevutsendingUtfoertHendelseConsumerForTest(
    behandlingService: BehandlingService
) : BrevutsendingUtfoertHendelseConsumer(behandlingService) {

    val latch = CountDownLatch(1)
    lateinit var payload: BrevutsendingUtfoertHendelse

    @KafkaListener(
        topics = ["\${kafka.topic.brevutsending-utfoert}"],
        containerFactory = "brevutsendingUtfoertHendelseListenerContainerFactory"
    )
    @Transactional
    override fun listen(record: BrevutsendingUtfoertHendelse) {
        super.listen(record)
        payload = record
        latch.countDown()
    }
}

