package no.nav.yrkesskade.saksbehandling.config

import no.nav.yrkesskade.saksbehandling.model.BrevutsendingBestiltHendelse
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@Configuration
class KafkaProducerConfig : AbstractKafkaConfig() {

    @Bean
    fun brevutsendingBestiltProducerFactory(
        kafkaProperties: KafkaProperties
    ): ProducerFactory<String, BrevutsendingBestiltHendelse> {
        return DefaultKafkaProducerFactory(kafkaProperties.buildProducerProperties())
    }

    @Bean
    fun brevutsendingBestiltKafkaTemplate(
        brevutsendingBestiltProducerFactory: ProducerFactory<String, BrevutsendingBestiltHendelse>
    ): KafkaTemplate<String, BrevutsendingBestiltHendelse> {
        return KafkaTemplate(brevutsendingBestiltProducerFactory)
    }
}