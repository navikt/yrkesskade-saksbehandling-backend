package no.nav.yrkesskade.saksbehandling.config

import no.nav.yrkesskade.saksbehandling.model.DokumentTilSaksbehandling
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.CommonContainerStoppingErrorHandler

@Configuration
class KafkaConsumerConfig : AbstractKafkaConfig() {

    @Bean
    fun dokumentTilSaksbehandlingListenerContainerFactory(
        kafkaProperties: KafkaProperties
    ): ConcurrentKafkaListenerContainerFactory<String, DokumentTilSaksbehandling> {

        val consumerProperties = kafkaProperties.buildConsumerProperties()
        val consumerFactory = DefaultKafkaConsumerFactory<String, DokumentTilSaksbehandling>(consumerProperties)

        return ConcurrentKafkaListenerContainerFactory<String, DokumentTilSaksbehandling>().apply {
            this.setConsumerFactory(consumerFactory)
            this.setCommonErrorHandler(CommonContainerStoppingErrorHandler())
            this.setRetryTemplate(retryTemplate())
        }
    }
}