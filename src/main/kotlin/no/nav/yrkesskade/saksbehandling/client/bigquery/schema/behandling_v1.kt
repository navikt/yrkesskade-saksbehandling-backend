package no.nav.yrkesskade.saksbehandling.client.bigquery.schema

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.treeToValue
import com.google.cloud.bigquery.InsertAllRequest
import com.google.cloud.bigquery.Schema
import java.time.Instant

val behandling_v1 = object : SchemaDefinition {
    override val schemaId: SchemaId = SchemaId(name = "saksbehandling", version = 1)
    val objectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())

    override fun define(): Schema = schema {
        string("behandlingId") {
            required()
            description("behandlingId")
        }
        string("journalpostId") {
            required()
            description("journalpostId")
        }
        string("utgaaendeJournalpostId") {
            required()
            description("journalpostId på dokument som sendes ut fra saksbehandling")
        }
        string("dokumentkategori") {
            required()
            description("Hva slags type dokument det gjelder, f.eks. tannlegeerklaering")
        }
        string("behandlingstype") {
            required()
            description("Hva slags type behandling")
        }
        string("behandlingsstatus") {
            required()
            description("Status på behandlingen")
        }
        string("enhetsnr") {
            required()
            description("Behandlende enhet")
        }
        boolean("overfoertLegacy") {
            required()
            description("Er behandlingen overført til Legacy-system")
        }
        timestamp("opprettet") {
            required()
            description("Tidsstempel for lagring av behandlingen")
        }
        timestamp("endret") {
            nullable()
            description("Tidsstempel for endring av behandlingen")
        }
    }

    override fun transform(payload: JsonNode): InsertAllRequest.RowToInsert {
        val behandlingPayload = objectMapper.treeToValue<BehandlingPayload>(payload)
        return InsertAllRequest.RowToInsert.of(
            mapOf(
                "behandlingId" to behandlingPayload.behandlingId,
                "journalpostId" to behandlingPayload.journalpostId,
                "utgaaendeJournalpostId" to behandlingPayload.utgaaendeJournalpostId,
                "dokumentkategori" to behandlingPayload.dokumentkategori,
                "behandlingstype" to behandlingPayload.behandlingstype,
                "behandlingsstatus" to behandlingPayload.behandlingsstatus,
                "enhetsnr" to behandlingPayload.enhetsnr,
                "overfoertLegacy" to behandlingPayload.overfoertLegacy,
                "opprettet" to behandlingPayload.opprettet,
                "endret" to behandlingPayload.endret
            )
        )
    }
}


data class BehandlingPayload(
    val behandlingId: String,
    val journalpostId: String,
    val utgaaendeJournalpostId: String?,
    val dokumentkategori: String,
    val behandlingstype: String,
    val behandlingsstatus: String,
    val enhetsnr: String,
    val overfoertLegacy: Boolean,
    val opprettet: Instant,
    val endret: Instant?,
)
