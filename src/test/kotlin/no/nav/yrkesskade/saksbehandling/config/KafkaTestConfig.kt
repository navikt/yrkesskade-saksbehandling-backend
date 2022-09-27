package no.nav.yrkesskade.saksbehandling.config

import no.nav.yrkesskade.saksbehandling.model.BrevutsendingUtfoertHendelse
import no.nav.yrkesskade.saksbehandling.model.DokumentTilSaksbehandlingHendelse
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@TestConfiguration
class KafkaTestConfig {

    @Bean
    fun brevutsendingUtfoertHendelseProducerFactory(
        kafkaProperties: KafkaProperties
    ): ProducerFactory<String, BrevutsendingUtfoertHendelse> {
        return DefaultKafkaProducerFactory(kafkaProperties.buildProducerProperties())
    }

    @Bean
    fun brevutsendingUtfoertHendelseKafkaTemplate(
        brevutsendingUtfoertHendelseProducerFactory: ProducerFactory<String,  BrevutsendingUtfoertHendelse>
    ): KafkaTemplate<String, BrevutsendingUtfoertHendelse> {
        return KafkaTemplate(brevutsendingUtfoertHendelseProducerFactory)
    }

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