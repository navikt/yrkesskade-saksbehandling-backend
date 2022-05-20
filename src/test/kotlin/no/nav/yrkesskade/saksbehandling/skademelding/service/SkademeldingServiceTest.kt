package no.nav.yrkesskade.saksbehandling.skademelding.service

import no.nav.yrkesskade.model.*
import no.nav.yrkesskade.saksbehandling.fixtures.skademelding.skademeldingMedTidspunkt
import no.nav.yrkesskade.saksbehandling.test.AbstractTest
import no.nav.yrkesskade.skademelding.model.Tidstype
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant


class SkademeldingServiceTest : AbstractTest() {

    @Autowired
    lateinit var skademeldingService: SkademeldingService

    @Test
    fun `lagre skademelding mottatt hendelse`() {
        val skademelding = skademeldingMedTidspunkt()
        val skademeldingInnsendtHendelse = SkademeldingInnsendtHendelse(
            skademelding = skademelding,
            beriketData = SkademeldingBeriketData(
                innmeldersOrganisasjonsnavn = "Test" to Systemkilde.ENHETSREGISTERET
            ),
            metadata = SkademeldingMetadata(
                kilde = "test",
                tidspunktMottatt = Instant.now(),
                spraak = Spraak.NB,
                navCallId = "test-call-id"
            )
        )
        val lagretSkademelding = skademeldingService.lagreSkademelding(skademeldingInnsendtHendelse)

        skademeldingService.hentSkademelding(lagretSkademelding.skademeldingId).also {
            assertThat(it).isNotNull()
            assertThat(it.ulykkessted).isNotNull()
            assertThat(it.virksomhetsAdressse).isNotNull()
            assertThat(it.stillingstitler?.size).isEqualTo(1)
            assertThat(it.skadedeDeler?.size).isEqualTo(1)
            assertThat(it.innmelderVirksomhetsnavn).isEqualTo("Test")
            assertThat(it.hendelseTidstype).isEqualTo(Tidstype.tidspunkt)
        }

    }

    @Test
    fun `hent skademelding som ikke finnes`() {
        assertThatThrownBy {
            skademeldingService.hentSkademelding(-1)
        }.isInstanceOf(Exception::class.java)
            .hasMessageContaining("Skademelding med id -1 finnes ikke")
    }
}