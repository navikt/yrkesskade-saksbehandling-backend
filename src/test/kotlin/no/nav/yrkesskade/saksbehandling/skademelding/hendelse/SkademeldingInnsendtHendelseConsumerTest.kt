package no.nav.yrkesskade.saksbehandling.skademelding.hendelse

import no.nav.yrkesskade.model.*
import no.nav.yrkesskade.saksbehandling.fixtures.skademelding.skademeldingMedTidspunkt
import no.nav.yrkesskade.saksbehandling.test.AbstractTest
import no.nav.yrkesskade.saksbehandling.test.TOPIC_NAME
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer
import java.time.Instant
import java.util.concurrent.TimeUnit

class SkademeldingInnsendtHendelseConsumerTest : AbstractTest() {

    @Autowired
    lateinit var skademeldingInnsendtHendelseConsumer: SkademeldingInnsendtHendelseConsumer

    @Autowired
    lateinit var skademeldingKafkaTemplate: KafkaTemplate<String, SkademeldingInnsendtHendelse>

    @Test
    fun `lytt på meldinger for skademelding og lagre til database`() {
        val skademeldingInnsendtHendelse = SkademeldingInnsendtHendelse(
            skademelding = skademeldingMedTidspunkt(),
            beriketData = SkademeldingBeriketData(
                innmeldersOrganisasjonsnavn = "Test" to Systemkilde.ENHETSREGISTERET
            ),
            metadata = SkademeldingMetadata(
                kilde = "test",
                tidspunktMottatt = Instant.now(),
                spraak = Spraak.NB,
                navCallId = "test-call-id"
            )
        )

        skademeldingKafkaTemplate.send(TOPIC_NAME, skademeldingInnsendtHendelse)
        skademeldingInnsendtHendelseConsumer.latch.await(10000, TimeUnit.MILLISECONDS);
        assertThat(skademeldingInnsendtHendelseConsumer.payload).isNotNull()
        assertThat(skademeldingInnsendtHendelseConsumer.payload?.skademelding?.innmelder?.norskIdentitetsnummer).isEqualTo(
            skademeldingInnsendtHendelse.skademelding.innmelder.norskIdentitetsnummer
        )
    }

}


@Configuration
class KafkaConfig(val kafkaProperties: KafkaProperties) {

    @Bean
    fun producerConfigs(): Map<String, Any> {
        val props: MutableMap<String, Any> = HashMap(kafkaProperties.buildProducerProperties())
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java
        return props
    }

    @Bean
    fun skademeldingProducerFactory(): ProducerFactory<String, SkademeldingInnsendtHendelse> {
        return DefaultKafkaProducerFactory(producerConfigs())
    }

    @Bean
    fun skademeldingKafkaTemplate(): KafkaTemplate<String, SkademeldingInnsendtHendelse> {
        return KafkaTemplate(skademeldingProducerFactory())
    }
}

