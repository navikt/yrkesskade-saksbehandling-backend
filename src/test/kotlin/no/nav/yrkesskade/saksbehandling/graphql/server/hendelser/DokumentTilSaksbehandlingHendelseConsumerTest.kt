package no.nav.yrkesskade.saksbehandling.graphql.server.hendelser

import no.nav.yrkesskade.saksbehandling.fixtures.journalpost.journalpostResultWithBrukerAktoerid
import no.nav.yrkesskade.saksbehandling.graphql.client.saf.SafClient
import no.nav.yrkesskade.saksbehandling.hendelser.DokumentTilSaksbehandlingHendelseConsumer
import no.nav.yrkesskade.saksbehandling.model.DokumentTilSaksbehandling
import no.nav.yrkesskade.saksbehandling.model.DokumentTilSaksbehandlingHendelse
import no.nav.yrkesskade.saksbehandling.model.DokumentTilSaksbehandlingMetadata
import no.nav.yrkesskade.saksbehandling.service.Dokumentmottak
import no.nav.yrkesskade.saksbehandling.test.AbstractTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

const val TOPIC = "dokument-til-saksbehandling-hendelse-test"

@Import(KafkaTestConfig::class)
class DokumentTilSaksbehandlingHendelseConsumerTest : AbstractTest() {

    @Autowired
    lateinit var consumer: DokumentTilSaksbehandlingHendelseConsumerForTest

    @MockBean
    lateinit var safClient: SafClient

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, DokumentTilSaksbehandlingHendelse>

    @Test
    fun listen() {
        `when`(safClient.hentOppdatertJournalpost(any())).thenReturn(journalpostResultWithBrukerAktoerid())

        val payload = DokumentTilSaksbehandlingHendelse(
            DokumentTilSaksbehandling(
                "1337",
                "9999",
            ),
            DokumentTilSaksbehandlingMetadata(UUID.randomUUID().toString())
        )

        kafkaTemplate.send(
            TOPIC,
            payload
        )

        // vent på at oppgavene i consumer blir fullført før vi gjennomfører testene
        consumer.latch.await(10000, TimeUnit.MILLISECONDS)
        assertThat(consumer.payload).isEqualTo(payload)
    }
}

@Primary
@Component
class DokumentTilSaksbehandlingHendelseConsumerForTest(
    dokumentmottak: Dokumentmottak
) : DokumentTilSaksbehandlingHendelseConsumer(dokumentmottak) {

    val latch = CountDownLatch(1)
    lateinit var payload: DokumentTilSaksbehandlingHendelse

    @KafkaListener(
        topics = [TOPIC],
        containerFactory = "dokumentTilSaksbehandlingHendelseListenerContainerFactory"
    )
    @Transactional
    override fun listen(record: DokumentTilSaksbehandlingHendelse) {
        super.listen(record)
        payload = record
        latch.countDown()
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