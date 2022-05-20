package no.nav.yrkesskade.saksbehandling.skademelding.config

import no.nav.yrkesskade.saksbehandling.config.AbstractKafkaConfig
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka

@EnableKafka
@Configuration
class CommonKafkaConfig : AbstractKafkaConfig() {
}