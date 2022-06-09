package no.nav.yrkesskade.saksbehandling.hendelser.oppgave

import no.nav.yrkesskade.saksbehandling.repository.BehandlingRepository
import no.nav.yrkesskade.saksbehandling.test.AbstractTest
import no.nav.yrkesskade.saksbehandling.test.TOPIC_NAME
import oppgaveUtenBehandlesAvApplikasjonAnnetFnr
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
import java.util.concurrent.TimeUnit

class OppgaveHendelserTest : AbstractTest() {

    @Autowired
    lateinit var oppgaveHendelser: OppgaveHendelser

    @Autowired
    lateinit var behandlingRepository: BehandlingRepository

    @Autowired
    lateinit var oppgaveKafkaTemplate: KafkaTemplate<String, String>

    @Test
    fun `lytt på meldinger for oppgave opprettet og lagre til database`() {
        val oppgaveUtenBehandlesAvApplikasjon = oppgaveUtenBehandlesAvApplikasjonAnnetFnr()

        oppgaveKafkaTemplate.send(TOPIC_NAME, oppgaveUtenBehandlesAvApplikasjon)
        oppgaveHendelser.latch.await(20000, TimeUnit.MILLISECONDS);
        assertThat(oppgaveHendelser.payload).isNotNull
        val behandling = behandlingRepository.findByOppgaveId("3")
        assertThat(behandling).isNotNull
    }
}


@Configuration
class KafkaConfig {

    @Bean
    fun producerConfigs(kafkaProperties: KafkaProperties): Map<String, Any> {
        val props: MutableMap<String, Any> = HashMap(kafkaProperties.buildProducerProperties())
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        return props
    }

    @Bean
    fun oppgaveProducerFactory(producerConfigs: Map<String, Any>): ProducerFactory<String, String> {
        return DefaultKafkaProducerFactory(producerConfigs)
    }

    @Bean
    fun oppgaveKafkaTemplate(oppgaveProducerFactory: ProducerFactory<String, String>): KafkaTemplate<String, String> {
        return KafkaTemplate(oppgaveProducerFactory)
    }
}

