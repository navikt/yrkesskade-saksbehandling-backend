package no.nav.yrkesskade.saksbehandling.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder


@Configuration
class JacksonConfig {

    @Bean
    @Primary
    fun objectMapper(builder: Jackson2ObjectMapperBuilder): ObjectMapper {
        val objectMapper: ObjectMapper = builder.build()
        objectMapper.registerModule(JavaTimeModule())
        return objectMapper
    }
}