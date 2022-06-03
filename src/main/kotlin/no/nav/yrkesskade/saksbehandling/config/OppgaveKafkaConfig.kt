package no.nav.yrkesskade.saksbehandling.config

import no.nav.yrkesskade.saksbehandling.hendelser.oppgave.model.OppgaveRecord
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.config.SaslConfigs
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.CommonContainerStoppingErrorHandler
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.testcontainers.shaded.org.bouncycastle.cms.RecipientId.password

@Configuration
class OppgaveKafkaConfig(@Value("\${SRV_YRKESSKADE_USERNAME:empty}") val username: String,
                         @Value("\${SRV_YRKESSKADE_PASSWORD:empty}") val password: String) : AbstractKafkaConfig() {

    @Bean
    fun oppgaveOpprettetHendelseListenerContainerFactory(kafkaProperties: KafkaProperties, environment: Environment):
            ConcurrentKafkaListenerContainerFactory<String, OppgaveRecord> {

        val consumerProperties = kafkaProperties.buildConsumerProperties().apply {
            this[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
            this[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
            if (environment.activeProfiles.intersect(listOf("local", "integration")).isEmpty()) {
                this[SaslConfigs.SASL_JAAS_CONFIG] =
                    "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"$username\" password=\"$password\";"
                this[SaslConfigs.SASL_MECHANISM] = "PLAIN"
                this[CommonClientConfigs.SECURITY_PROTOCOL_CONFIG] = "SASL_SSL"
            }
        }
        val consumerFactory = DefaultKafkaConsumerFactory<String, OppgaveRecord>(consumerProperties)

        return ConcurrentKafkaListenerContainerFactory<String, OppgaveRecord>().apply {
            this.setConsumerFactory(consumerFactory)
            this.setCommonErrorHandler(CommonContainerStoppingErrorHandler())
            this.setRetryTemplate(retryTemplate())
        }
    }
}