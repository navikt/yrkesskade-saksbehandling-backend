package no.nav.yrkesskade.saksbehandling.service

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import no.nav.yrkesskade.kodeverk.model.KodeverdiDto
import no.nav.yrkesskade.saksbehandling.client.Kodeverkklient
import no.nav.yrkesskade.saksbehandling.fixtures.behandlingstyper
import no.nav.yrkesskade.saksbehandling.model.dto.KodeverkTidData
import no.nav.yrkesskade.saksbehandling.model.dto.KodeverkTypeKategori
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.util.ReflectionTestUtils
import java.time.Instant

@Suppress("NonAsciiCharacters", "INTEGER_OPERATOR_RESOLVE_WILL_CHANGE", "UNCHECKED_CAST")
@ExtendWith(MockKExtension::class)
internal class KodeverkServiceTest {

    private val behandlingstype = "behandlingstype"
    private val kategoriBlank = ""
    private val bokmaal = "nb"
    private val keyBehandlingstype = KodeverkTypeKategori(behandlingstype, kategoriBlank)

    private val kodeverkklientMock: Kodeverkklient = mockk()

    private val service = KodeverkService(kodeverkklientMock)


    @BeforeEach
    fun setup() {
        every { kodeverkklientMock.hentKodeverk(any(), any(), any()) } returns behandlingstyper()
    }


    @Test
    fun `skal hente behandlingstype fra map når kodeverket finnes fra før`() {
        val map = mutableMapOf(keyBehandlingstype to KodeverkTidData(behandlingstyper(), Instant.now()))
        ReflectionTestUtils.setField(service, "kodeverkMap", map)

        service.hentKodeverk(behandlingstype, kategoriBlank, bokmaal)
        verify(exactly = 0) { kodeverkklientMock.hentKodeverk(any(), any(), any()) }
    }

    @Test
    fun `skal hente behandlingstype fra api når det er lenge siden kodeverket ble hentet`() {
        val forLengeSiden = Instant.MIN
        val mapMedUtløptKodeverk = mutableMapOf(keyBehandlingstype to KodeverkTidData(behandlingstyper(), forLengeSiden))
        ReflectionTestUtils.setField(service, "kodeverkMap", mapMedUtløptKodeverk)

        val map = (ReflectionTestUtils.getField(service, "kodeverkMap") as MutableMap<KodeverkTypeKategori, KodeverkTidData>)
        val kodeverkTidData = map[keyBehandlingstype]!!
        // Data finnes...
        assertThat(kodeverkTidData.data["veiledning"]).isEqualTo(KodeverdiDto("veiledning", "Veiledning"))
        // ...men er hentet for mer enn x minutter siden
        assertThat(kodeverkTidData.hentetTid).isBefore(Instant.now().minusSeconds(60*60))

        service.hentKodeverk(behandlingstype, kategoriBlank, bokmaal)
        verify(exactly = 1) { kodeverkklientMock.hentKodeverk(any(), any(), any()) }
    }

    @Test
    fun `skal hente behandlingstype fra api når kodeverket ikke finnes fra før`() {
        val map = (ReflectionTestUtils.getField(service, "kodeverkMap") as MutableMap<KodeverkTypeKategori, KodeverkTidData>)
        val kodeverkTidData = map[keyBehandlingstype]
        // Data finnes ikke...
        assertThat(kodeverkTidData).isNull()

        service.hentKodeverk(behandlingstype, kategoriBlank, bokmaal)
        verify(exactly = 1) { kodeverkklientMock.hentKodeverk(any(), any(), any()) }
    }

    @Test
    fun `skal returnere tom map når kodeverket ikke finnes i api`() {
        every { kodeverkklientMock.hentKodeverk(any(), any(), any()) } returns emptyMap()

        val landKodeverk = service.hentKodeverk(behandlingstype, kategoriBlank, bokmaal)
        verify(exactly = 1) { kodeverkklientMock.hentKodeverk(any(), any(), any()) }
        assertThat(landKodeverk).isEmpty()
    }

}