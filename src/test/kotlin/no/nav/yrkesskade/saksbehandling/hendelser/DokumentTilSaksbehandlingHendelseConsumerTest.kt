package no.nav.yrkesskade.saksbehandling.hendelser

import no.nav.yrkesskade.saksbehandling.model.DokumentTilSaksbehandling
import no.nav.yrkesskade.saksbehandling.model.DokumentTilSaksbehandlingMetadata
import no.nav.yrkesskade.saksbehandling.test.AbstractTest
import org.junit.jupiter.api.Test

import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.timeout
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.kafka.core.KafkaTemplate
import java.util.UUID

class DokumentTilSaksbehandlingHendelseConsumerTest : AbstractTest() {

    @SpyBean
    lateinit var consumer: DokumentTilSaksbehandlingHendelseConsumer

    @Value("\${kafka.topic.dokument-til-saksbehandling}")
    lateinit var topic: String

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, DokumentTilSaksbehandling>

    @Test
    fun listen() {
        kafkaTemplate.send(
            topic,
            DokumentTilSaksbehandling(
                "1337",
                "9999",
                DokumentTilSaksbehandlingMetadata(UUID.randomUUID().toString())
            )
        )

        Mockito.verify(consumer, timeout(20000L).times(1)).listen(any())
    }
}