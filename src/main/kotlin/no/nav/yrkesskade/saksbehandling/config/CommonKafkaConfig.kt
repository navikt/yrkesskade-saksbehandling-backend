package no.nav.yrkesskade.saksbehandling.config

import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka

@EnableKafka
@Configuration
class CommonKafkaConfig : AbstractKafkaConfig() {
}