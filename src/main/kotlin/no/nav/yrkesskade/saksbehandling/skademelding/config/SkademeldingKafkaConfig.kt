package no.nav.yrkesskade.saksbehandling.skademelding.config

import no.nav.yrkesskade.model.SkademeldingInnsendtHendelse
import no.nav.yrkesskade.saksbehandling.config.AbstractKafkaConfig
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.CommonContainerStoppingErrorHandler
import org.springframework.kafka.support.serializer.JsonDeserializer


@Configuration
class SkademeldingKafkaConfig : AbstractKafkaConfig() {

    @Bean
    fun skademeldingInnsendtHendelseListenerContainerFactory(kafkaProperties: KafkaProperties):
            ConcurrentKafkaListenerContainerFactory<String, SkademeldingInnsendtHendelse> {

        val consumerProperties = kafkaProperties.buildConsumerProperties().apply {
            this[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
        }
        val consumerFactory = DefaultKafkaConsumerFactory<String, SkademeldingInnsendtHendelse>(consumerProperties)

        return ConcurrentKafkaListenerContainerFactory<String, SkademeldingInnsendtHendelse>().apply {
            this.setConsumerFactory(consumerFactory)
            this.setCommonErrorHandler(CommonContainerStoppingErrorHandler())
            this.setRetryTemplate(retryTemplate())
        }
    }
}
