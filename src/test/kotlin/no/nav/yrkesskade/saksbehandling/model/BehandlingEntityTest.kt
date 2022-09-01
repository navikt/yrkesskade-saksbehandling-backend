package no.nav.yrkesskade.saksbehandling.model

import com.expediagroup.graphql.generated.enums.BrukerIdType
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import no.nav.yrkesskade.saksbehandling.fixtures.genererBehandling
import no.nav.yrkesskade.saksbehandling.fixtures.genererSak
import no.nav.yrkesskade.saksbehandling.model.dto.BehandlingDto
import no.nav.yrkesskade.saksbehandling.util.kodeverk.KodeverdiMapper
import no.nav.yrkesskade.saksbehandling.util.kodeverk.KodeverkHolder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit

@ExtendWith(MockKExtension::class)
internal class BehandlingEntityTest {

    private val kodeverkHolderMock: KodeverkHolder = mockk()
    private val kodeverkMapper = KodeverdiMapper(kodeverkHolderMock)

    @Test
    fun `skal mappe entity til dto`() {
        every { kodeverkHolderMock.mapKodeTilVerdi("veiledning", "behandlingstype") } returns "Veiledning"
        every { kodeverkHolderMock.mapKodeTilVerdi("underBehandling", "behandlingsstatus") } returns "Under behandling"
        every { kodeverkHolderMock.mapKodeTilVerdi("ikkePaabegynt", "framdriftsstatus") } returns "Ikke påbegynt"

        val sak = genererSak()
        val entity = genererBehandling(123L, "Mr Ansvarlig", Behandlingsstatus.UNDER_BEHANDLING, sak)

        val dto = entity.toBehandlingDto(kodeverkMapper)

        assertThat(dto.behandlingId).isEqualTo(123L)
        assertThat(dto.tema).isEqualTo("YRK")
        assertThat(dto.brukerId).isEqualTo("12345")
        assertThat(dto.brukerIdType).isEqualTo(BrukerIdType.AKTOERID)
        assertThat(dto.behandlendeEnhet).isEqualTo("9999")
        assertThat(dto.saksbehandlingsansvarligIdent).isEqualTo("Mr Ansvarlig")
        assertThat(dto.behandlingstype).isEqualTo("Veiledning")
        assertThat(dto.status).isEqualTo("Under behandling")
        assertThat(dto.behandlingsfrist.truncatedTo(ChronoUnit.DAYS)).isEqualTo(Instant.now().plus(30, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS))
        assertThat(dto.journalpostId).isEqualTo("213123123")
        assertThat(dto.dokumentkategori).isEqualTo("enFinKategori")
        assertThat(dto.systemreferanse).isEqualTo("referanse")
        assertThat(dto.framdriftsstatus).isEqualTo("Ikke påbegynt")
        assertThat(dto.opprettetTidspunkt.truncatedTo(ChronoUnit.DAYS)).isEqualTo(Instant.now().truncatedTo(ChronoUnit.DAYS))
        assertThat(dto.opprettetAv).isEqualTo("test")
        assertThat(dto.endretAv).isNull()
        assertThat(dto.sak).isEqualTo(sak)
        assertThat(dto.behandlingResultater).isEmpty()
    }

}