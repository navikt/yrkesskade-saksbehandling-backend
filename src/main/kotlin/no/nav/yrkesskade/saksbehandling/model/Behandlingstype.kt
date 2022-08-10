package no.nav.yrkesskade.saksbehandling.model

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer

@JsonSerialize(using = BehandlingstypeSerializer::class)
@JsonDeserialize(using = BehandlingstypeDeserializer::class)
enum class Behandlingstype(val verdi: String) {
    JOURNALFOERING("Journalføring"),
    VEILEDNING("Veiledning"),
    KRAV_MELDING("Krav"),
    KLAGE("Klage"),
    ANKE("Anke"),
    INNSYN("Innsyn"),
    GJENOPPRETTING("Gjenoppretting"),
    REVURDERING("Revurdering"),
    TILBAKEKREVING("Tilbakekreving")
}

class BehandlingstypeSerializer : StdSerializer<Behandlingstype>(Behandlingstype::class.java) {

    override fun serialize(
        behandlingstype: Behandlingstype, generator: JsonGenerator, provider: SerializerProvider?
    ) {
        generator.writeStartObject()
        generator.writeFieldName("kode")
        generator.writeString(behandlingstype.name)
        generator.writeFieldName("verdi")
        generator.writeString(behandlingstype.verdi)
        generator.writeEndObject()
    }
}

class BehandlingstypeDeserializer : StdDeserializer<Behandlingstype>(Behandlingstype::class.java) {
    override fun deserialize(jsonParser: JsonParser, ctxt: DeserializationContext?): Behandlingstype? {
        val node: JsonNode = jsonParser.codec.readTree(jsonParser)
        val kode: String = node.get("kode").asText()

        for (behandlingstype in Behandlingstype.values()) {
            if (behandlingstype.name == kode) {
                return behandlingstype
            }
        }
        return null
    }
}