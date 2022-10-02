package no.nav.yrkesskade.saksbehandling.client.bigquery.schema

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.reflect.full.memberProperties

internal class SchemaTest {

    private val objectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())

    @Test
    internal fun `payload mappes riktig til en behandling_v1 row`() {
        val payload = BehandlingPayload(
            behandlingId = "1",
            journalpostId = "100",
            utgaaendeJournalpostId = "200",
            dokumentkategori = "tannlegeerklaering",
            behandlingstype = "journalfoering",
            behandlingsstatus = "ferdig",
            enhetsnr = "1234",
            overfoertLegacy = false,
            opprettet = Instant.now(),
            endret = Instant.now()
        )

        val content = behandling_v1.transform(objectMapper.valueToTree(payload)).content
        assertThat(content["behandlingId"]).isEqualTo(payload.behandlingId)
        assertThat(content["journalpostId"]).isEqualTo(payload.journalpostId)
        assertThat(content["utgaaendeJournalpostId"]).isEqualTo(payload.utgaaendeJournalpostId)
        assertThat(content["dokumentkategori"]).isEqualTo(payload.dokumentkategori)
        assertThat(content["behandlingstype"]).isEqualTo(payload.behandlingstype)
        assertThat(content["behandlingsstatus"]).isEqualTo(payload.behandlingsstatus)
        assertThat(content["enhetsnr"]).isEqualTo(payload.enhetsnr)
        assertThat(content["overfoertLegacy"]).isEqualTo(payload.overfoertLegacy)
        assertThat(content["opprettet"]).isEqualTo(payload.opprettet)
        assertThat(content["endret"]).isEqualTo(payload.endret)
    }

    @Test
    internal fun `behandling_v1 schema skal ha samme antall felter som payload`() {
        assertThat(BehandlingPayload::class.memberProperties.size)
            .isEqualTo(behandling_v1.define().fields.size)
    }
}
