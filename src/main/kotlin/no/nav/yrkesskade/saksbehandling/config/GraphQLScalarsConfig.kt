package no.nav.yrkesskade.saksbehandling.config

import graphql.schema.GraphQLScalarType
import no.nav.yrkesskade.saksbehandling.graphql.common.scalar.InstantCoercing
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GraphQLScalarsConfig {

    @Bean
    fun instantScalar() : GraphQLScalarType
    {
        return GraphQLScalarType.newScalar()
            .name("Instant")
            .description("Java 8 Instant as scalar.")
            .coercing(InstantCoercing())
            .build()
    }
}