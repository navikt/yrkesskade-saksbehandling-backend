package no.nav.yrkesskade.saksbehandling.hendelser

import no.nav.yrkesskade.saksbehandling.fixtures.journalpostResultWithBrukerAktoerid
import no.nav.yrkesskade.saksbehandling.graphql.client.SafClient
import no.nav.yrkesskade.saksbehandling.model.DokumentTilSaksbehandling
import no.nav.yrkesskade.saksbehandling.model.DokumentTilSaksbehandlingHendelse
import no.nav.yrkesskade.saksbehandling.model.DokumentTilSaksbehandlingMetadata
import no.nav.yrkesskade.saksbehandling.test.AbstractTest
import org.junit.jupiter.api.Test

import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.timeout
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import java.util.UUID

@Import(KafkaTestConfig::class)
class DokumentTilSaksbehandlingHendelseConsumerTest : AbstractTest() {

    @SpyBean
    lateinit var consumer: DokumentTilSaksbehandlingHendelseConsumer

    @MockBean
    lateinit var safClient: SafClient

    @Value("\${kafka.topic.dokument-til-saksbehandling}")
    lateinit var topic: String

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, DokumentTilSaksbehandlingHendelse>

    @Test
    fun listen() {
        `when`(safClient.hentOppdatertJournalpost(any())).thenReturn(journalpostResultWithBrukerAktoerid())

        kafkaTemplate.send(
            topic,
            DokumentTilSaksbehandlingHendelse(
                DokumentTilSaksbehandling(
                    "1337",
                    "9999",
                ),
                DokumentTilSaksbehandlingMetadata(UUID.randomUUID().toString())
            )
        )

        Mockito.verify(consumer, timeout(20000L).times(1)).listen(any())
    }
}

@TestConfiguration
class KafkaTestConfig {

    @Bean
    fun dokumentTilSaksbehandlingHendelseProducerFactory(
        properties: KafkaProperties
    ): DefaultKafkaProducerFactory<String, DokumentTilSaksbehandlingHendelse> =
        DefaultKafkaProducerFactory(properties.buildProducerProperties())

    @Bean
    fun dokumentTilSaksbehandlingHendelse(
        dokumentTilSaksbehandlingHendelseProducerFactory: ProducerFactory<String, DokumentTilSaksbehandlingHendelse>
    ): KafkaTemplate<String, DokumentTilSaksbehandlingHendelse> =
        KafkaTemplate(dokumentTilSaksbehandlingHendelseProducerFactory)
}