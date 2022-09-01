package no.nav.yrkesskade.saksbehandling.util.kodeverk

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import no.nav.yrkesskade.saksbehandling.fixtures.behandlingstyper
import no.nav.yrkesskade.saksbehandling.model.Behandlingstype
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

        kodeverkHolder = KodeverkHolder.init(kodeverkService = kodeverkServiceMock)

        kodeverdiMapper = KodeverdiMapper(kodeverkHolder)
    }

    @Test
    fun `skal mappe behandlingstyper`() {
        assertThat(kodeverdiMapper.mapBehandlingstype(Behandlingstype.ANKE)).isEqualTo("Anke")
    }

}