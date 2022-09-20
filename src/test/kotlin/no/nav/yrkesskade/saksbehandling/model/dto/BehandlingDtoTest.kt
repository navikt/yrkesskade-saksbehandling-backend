package no.nav.yrkesskade.saksbehandling.model.dto

import com.expediagroup.graphql.generated.enums.BrukerIdType
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import no.nav.yrkesskade.saksbehandling.fixtures.genererBehandling
import no.nav.yrkesskade.saksbehandling.fixtures.genererSak
import no.nav.yrkesskade.saksbehandling.model.Behandlingsstatus
import no.nav.yrkesskade.saksbehandling.model.Behandlingstype
import no.nav.yrkesskade.saksbehandling.model.Framdriftsstatus
import no.nav.yrkesskade.saksbehandling.util.kodeverk.KodeverdiMapper
import no.nav.yrkesskade.saksbehandling.util.kodeverk.KodeverkHolder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Instant
import java.time.temporal.ChronoUnit

@ExtendWith(MockKExtension::class)
internal class BehandlingDtoTest {


    @Test
    fun `skal mappe entity til dto`() {
        val sak = genererSak()
        val entity = genererBehandling(123L, "Mr Ansvarlig", Behandlingsstatus.UNDER_BEHANDLING, sak)

        val dto = BehandlingDto.fromEntity(entity)

        assertThat(dto.behandlingId).isEqualTo(123L)
        assertThat(dto.tema).isEqualTo("YRK")
        assertThat(dto.brukerId).isEqualTo("12345")
        assertThat(dto.brukerIdType).isEqualTo(BrukerIdType.AKTOERID)
        assertThat(dto.behandlendeEnhet).isEqualTo("9999")
        assertThat(dto.saksbehandlingsansvarligIdent).isEqualTo("Mr Ansvarlig")
        assertThat(dto.behandlingstype).isEqualTo(Behandlingstype.VEILEDNING.kode)
        assertThat(dto.status).isEqualTo(Behandlingsstatus.UNDER_BEHANDLING.kode)
        assertThat(dto.behandlingsfrist.truncatedTo(ChronoUnit.DAYS)).isEqualTo(
            Instant.now().plus(30, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS))
        assertThat(dto.journalpostId).isEqualTo("213123123")
        assertThat(dto.dokumentkategori).isEqualTo("enFinKategori")
        assertThat(dto.systemreferanse).isEqualTo("referanse")
        assertThat(dto.framdriftsstatus).isEqualTo(Framdriftsstatus.IKKE_PAABEGYNT.kode)
        assertThat(dto.opprettetTidspunkt.truncatedTo(ChronoUnit.DAYS)).isEqualTo(
            Instant.now().truncatedTo(ChronoUnit.DAYS))
        assertThat(dto.opprettetAv).isEqualTo("test")
        assertThat(dto.endretAv).isNull()
        assertThat(dto.sak).isEqualTo(sak)
        assertThat(dto.behandlingResultater).isEmpty()
    }

}