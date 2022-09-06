package no.nav.yrkesskade.saksbehandling.util.kodeverk

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import no.nav.yrkesskade.saksbehandling.fixtures.behandlingstyper
import no.nav.yrkesskade.saksbehandling.service.KodeverkService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class KodeverkHolderTest {

    private val kodeverkServiceMock: KodeverkService = mockk()


    @BeforeEach
    fun setup() {
        every { kodeverkServiceMock.hentKodeverk(type = any(), kategori = any(), spraak = any()) } returns behandlingstyper()
    }

    @Test
    fun `init skal fylle kodeverkholder med data`() {
        val kodeverkHolder = KodeverkHolder.init(null, kodeverkServiceMock)
        assertThat(kodeverkHolder).isNotNull

        val kodeVerdi = kodeverkHolder.mapKodeTilVerdi("veiledning", "behandlingstype")
        assertThat(kodeVerdi).isEqualTo("Veiledning")
    }

}