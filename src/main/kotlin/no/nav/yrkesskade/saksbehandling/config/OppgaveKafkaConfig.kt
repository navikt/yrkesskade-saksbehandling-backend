package no.nav.yrkesskade.saksbehandling.config

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.yrkesskade.saksbehandling.hendelser.oppgave.model.OppgaveRecord
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.CommonContainerStoppingErrorHandler
import org.springframework.kafka.support.serializer.JsonDeserializer

@Configuration
class OppgaveKafkaConfig(val objectMapper: ObjectMapper) : AbstractKafkaConfig() {

    @Bean
    fun oppgaveOpprettetHendelseListenerContainerFactory(kafkaProperties: KafkaProperties):
            ConcurrentKafkaListenerContainerFactory<String, OppgaveRecord> {

        val consumerProperties = kafkaProperties.buildConsumerProperties().apply {
            this[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
            this[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        }
        val consumerFactory = DefaultKafkaConsumerFactory<String, OppgaveRecord>(consumerProperties) //, StringDeserializer(), JsonDeserializer(objectMapper))

        return ConcurrentKafkaListenerContainerFactory<String, OppgaveRecord>().apply {
            this.setConsumerFactory(consumerFactory)
            this.setCommonErrorHandler(CommonContainerStoppingErrorHandler())
            this.setRetryTemplate(retryTemplate())
        }
    }
}