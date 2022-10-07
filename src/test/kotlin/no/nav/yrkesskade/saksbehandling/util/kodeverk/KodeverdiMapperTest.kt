package no.nav.yrkesskade.saksbehandling.util.kodeverk

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import no.nav.yrkesskade.saksbehandling.fixtures.behandlingsstatus
import no.nav.yrkesskade.saksbehandling.fixtures.behandlingstyper
import no.nav.yrkesskade.saksbehandling.fixtures.dokumentkategori
import no.nav.yrkesskade.saksbehandling.fixtures.framdriftsstatus
import no.nav.yrkesskade.saksbehandling.model.Behandlingsstatus
import no.nav.yrkesskade.saksbehandling.model.Behandlingstype
import no.nav.yrkesskade.saksbehandling.model.Framdriftsstatus
import no.nav.yrkesskade.saksbehandling.service.KodeverkService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class KodeverdiMapperTest {

    private val kodeverkServiceMock: KodeverkService = mockk()

    private lateinit var kodeverkHolder: KodeverkHolder

    private lateinit var kodeverdiMapper: KodeverdiMapper


    @BeforeEach
    fun setup() {
        every { kodeverkServiceMock.hentKodeverk("behandlingstype", null) } returns behandlingstyper()
        every { kodeverkServiceMock.hentKodeverk("behandlingsstatus", null) } returns behandlingsstatus()
        every { kodeverkServiceMock.hentKodeverk("framdriftsstatus", null) } returns framdriftsstatus()
        every { kodeverkServiceMock.hentKodeverk("dokumenttype", null) } returns dokumentkategori()

        kodeverkHolder = KodeverkHolder.init(kodeverkService = kodeverkServiceMock)

        kodeverdiMapper = KodeverdiMapper(kodeverkHolder)
    }

    @Test
    fun `skal mappe behandlingstyper`() {
        assertThat(kodeverdiMapper.mapBehandlingstype(Behandlingstype.ANKE)).isEqualTo("Anke")
        assertThat(kodeverdiMapper.mapBehandlingstype(Behandlingstype.GJENOPPRETTING)).isEqualTo("Gjenoppretting")
        assertThat(kodeverdiMapper.mapBehandlingstype(Behandlingstype.INNSYN)).isEqualTo("Innsyn")
        assertThat(kodeverdiMapper.mapBehandlingstype(Behandlingstype.KLAGE)).isEqualTo("Klage")
        assertThat(kodeverdiMapper.mapBehandlingstype(Behandlingstype.KRAV_MELDING)).isEqualTo("Krav/Melding")
        assertThat(kodeverdiMapper.mapBehandlingstype(Behandlingstype.JOURNALFOERING)).isEqualTo("Journalføring")
        assertThat(kodeverdiMapper.mapBehandlingstype(Behandlingstype.REVURDERING)).isEqualTo("Revurdering")
        assertThat(kodeverdiMapper.mapBehandlingstype(Behandlingstype.TILBAKEKREVING)).isEqualTo("Tilbakekreving")
        assertThat(kodeverdiMapper.mapBehandlingstype(Behandlingstype.VEILEDNING)).isEqualTo("Veiledning")
    }

    @Test
    fun `skal mappe behandlingsstatus`() {
        assertThat(kodeverdiMapper.mapBehandlingsstatus(Behandlingsstatus.IKKE_PAABEGYNT)).isEqualTo("Ikke påbegynt")
        assertThat(kodeverdiMapper.mapBehandlingsstatus(Behandlingsstatus.UNDER_BEHANDLING)).isEqualTo("Under behandling")
        assertThat(kodeverdiMapper.mapBehandlingsstatus(Behandlingsstatus.FERDIG)).isEqualTo("Ferdig")
    }

    @Test
    fun `skal mappe framdriftsstatus`() {
        assertThat(kodeverdiMapper.mapFramdriftsstatus(Framdriftsstatus.IKKE_PAABEGYNT)).isEqualTo("Ikke påbegynt")
        assertThat(kodeverdiMapper.mapFramdriftsstatus(Framdriftsstatus.UNDER_ARBEID)).isEqualTo("Under arbeid")
        assertThat(kodeverdiMapper.mapFramdriftsstatus(Framdriftsstatus.PAA_VENT)).isEqualTo("På vent")
        assertThat(kodeverdiMapper.mapFramdriftsstatus(Framdriftsstatus.AVVENTER_SVAR)).isEqualTo("Avventer svar")
    }

    @Test
    fun `skal mappe dokumentkategori`() {
        assertThat(kodeverdiMapper.mapDokumentkategori("tannlegeerklaering")).isEqualTo("Tannlegeerklæring")
        assertThat(kodeverdiMapper.mapDokumentkategori("veiledningsbrevTannlegeerklaering")).isEqualTo("Veiledningsbrev Tannlegeerklæring")
        assertThat(kodeverdiMapper.mapDokumentkategori("veiledningsbrevArbeidstilsynsmelding")).isEqualTo("Veiledningsbrev Arbeidstilsynsmelding")
    }

}