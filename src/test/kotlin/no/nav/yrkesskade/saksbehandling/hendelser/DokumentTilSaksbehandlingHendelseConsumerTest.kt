package no.nav.yrkesskade.saksbehandling.hendelser

import no.nav.yrkesskade.saksbehandling.config.KafkaTestConfig
import no.nav.yrkesskade.saksbehandling.fixtures.behandlingsstatus
import no.nav.yrkesskade.saksbehandling.fixtures.behandlingstyper
import no.nav.yrkesskade.saksbehandling.fixtures.dokumentkategori
import no.nav.yrkesskade.saksbehandling.fixtures.framdriftsstatus
import no.nav.yrkesskade.saksbehandling.fixtures.journalpost.journalpostResultWithBrukerAktoerid
import no.nav.yrkesskade.saksbehandling.graphql.client.saf.SafClient
import no.nav.yrkesskade.saksbehandling.model.DokumentTilSaksbehandling
import no.nav.yrkesskade.saksbehandling.model.DokumentTilSaksbehandlingHendelse
import no.nav.yrkesskade.saksbehandling.model.DokumentTilSaksbehandlingMetadata
import no.nav.yrkesskade.saksbehandling.service.Dokumentmottak
import no.nav.yrkesskade.saksbehandling.service.KodeverkService
import no.nav.yrkesskade.saksbehandling.service.PdlService
import no.nav.yrkesskade.saksbehandling.test.AbstractTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

const val DOKUMENT_TIL_SAKSBEHANDLING_TOPIC = "dokument-til-saksbehandling-hendelse-test"

@Import(KafkaTestConfig::class)
class DokumentTilSaksbehandlingHendelseConsumerTest : AbstractTest() {

    @Autowired
    lateinit var consumer: DokumentTilSaksbehandlingHendelseConsumerForTest

    @MockBean
    lateinit var safClient: SafClient

    @MockBean
    lateinit var pdlService: PdlService

    @MockBean
    lateinit var kodeverkService: KodeverkService

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, DokumentTilSaksbehandlingHendelse>

    @BeforeEach
    fun setUp() {
        mockKodeverk()
    }

    fun mockKodeverk() {
        `when`(kodeverkService.hentKodeverk(eq("behandlingstype"), eq(null), any())).thenReturn(behandlingstyper())
        `when`(kodeverkService.hentKodeverk(eq("behandlingsstatus"), eq(null), any())).thenReturn(
            behandlingsstatus()
        )
        `when`(kodeverkService.hentKodeverk(eq("framdriftsstatus"), eq(null), any())).thenReturn(
            framdriftsstatus()
        )
        `when`(kodeverkService.hentKodeverk(eq("dokumenttype"), eq(null), any())).thenReturn(dokumentkategori())
    }

    @Test
    fun listen() {
        val journalpost = journalpostResultWithBrukerAktoerid()
        `when`(safClient.hentOppdatertJournalpost(any())).thenReturn(journalpost)
        `when`(pdlService.hentFoedselsnummerMedMaskinTilMaskinToken(eq(journalpost.journalpost!!.bruker!!.id!!))).thenReturn("01010112345")

        val payload = DokumentTilSaksbehandlingHendelse(
            DokumentTilSaksbehandling(
                "1337",
                "9999",
            ),
            DokumentTilSaksbehandlingMetadata(UUID.randomUUID().toString())
        )

        kafkaTemplate.send(DOKUMENT_TIL_SAKSBEHANDLING_TOPIC, payload)

        // vent på at oppgavene i consumer blir fullført før vi gjennomfører testene
        consumer.latch.await(10000, TimeUnit.MILLISECONDS)
        assertThat(consumer.payload).isEqualTo(payload)
    }
}

@Primary
@Component
class DokumentTilSaksbehandlingHendelseConsumerForTest(
    dokumentmottak: Dokumentmottak
) : DokumentTilSaksbehandlingHendelseConsumer(dokumentmottak) {

    val latch = CountDownLatch(1)
    lateinit var payload: DokumentTilSaksbehandlingHendelse

    @KafkaListener(
        topics = [DOKUMENT_TIL_SAKSBEHANDLING_TOPIC],
        containerFactory = "dokumentTilSaksbehandlingHendelseListenerContainerFactory"
    )
    @Transactional
    override fun listen(record: DokumentTilSaksbehandlingHendelse) {
        super.listen(record)
        payload = record
        latch.countDown()
    }
}