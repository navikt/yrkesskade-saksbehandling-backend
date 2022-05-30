package no.nav.yrkesskade.saksbehandling.graphql.common.scalar

import graphql.Internal
import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
import graphql.schema.CoercingSerializeException
import java.time.Instant

@Internal
class InstantCoercing() :
    Coercing<Instant, String> {

    override fun serialize(input: Any): String {
        return if (input is Instant) {
            input.toString()
        } else {
            throw CoercingSerializeException("Invalid value '$input' for Instant")
        }
    }

    override fun parseValue(input: Any): Instant {
        val result = Instant.parse(input.toString())
        return result ?: throw CoercingParseValueException("Invalid value '$input' for Instant")
    }

    override fun parseLiteral(input: Any): Instant {
        val value = (input as StringValue).value
        val result = Instant.parse(value)
        return result ?: throw CoercingParseLiteralException("Invalid value '$input' for Instant")
    }
}
