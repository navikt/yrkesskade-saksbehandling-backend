package no.nav.yrkesskade.saksbehandling.config

import com.zhokhov.graphql.datetime.GraphqlLocalDateCoercing
import graphql.schema.GraphQLScalarType
import no.nav.yrkesskade.saksbehandling.graphql.common.scalar.InstantCoercing
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.format.DateTimeFormatter

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

    @Bean
    fun localDateScalar() : GraphQLScalarType
    {
        return GraphQLScalarType.newScalar()
            .name("LocalDate")
            .description("Java 8 LocalDate as scalar.")
            .coercing(GraphqlLocalDateCoercing(false, DateTimeFormatter.ISO_DATE))
            .build()
    }
}