package no.nav.yrkesskade.saksbehandling.hendelser

import no.nav.yrkesskade.saksbehandling.config.KafkaTestConfig
import no.nav.yrkesskade.saksbehandling.fixtures.behandlingsstatus
import no.nav.yrkesskade.saksbehandling.fixtures.behandlingstyper
import no.nav.yrkesskade.saksbehandling.fixtures.dokumentkategori
import no.nav.yrkesskade.saksbehandling.fixtures.framdriftsstatus
import no.nav.yrkesskade.saksbehandling.model.BrevutsendingUtfoertHendelse
import no.nav.yrkesskade.saksbehandling.model.BrevutsendingUtfoertMetadata
import no.nav.yrkesskade.saksbehandling.service.BehandlingService
import no.nav.yrkesskade.saksbehandling.service.KodeverkService
import no.nav.yrkesskade.saksbehandling.test.AbstractTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
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

const val BREVUTSENDING_UTFOERT_TOPIC = "brevutsending-utfoert-hendelse-test"

@Import(KafkaTestConfig::class)
class BrevutsendingUtfoertHendelseConsumerTest : AbstractTest() {

    @Autowired
    lateinit var consumer: BrevutsendingUtfoertHendelseConsumerForTest

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, BrevutsendingUtfoertHendelse>

    @MockBean
    lateinit var behandlingService: BehandlingService

    @MockBean
    lateinit var kodeverkService: KodeverkService

    @BeforeEach
    fun setUp() {
        mockKodeverk()
    }

    fun mockKodeverk() {
        Mockito.`when`(kodeverkService.hentKodeverk(eq("behandlingstype"), eq(null), any())).thenReturn(behandlingstyper())
        Mockito.`when`(kodeverkService.hentKodeverk(eq("behandlingsstatus"), eq(null), any())).thenReturn(
            behandlingsstatus()
        )
        Mockito.`when`(kodeverkService.hentKodeverk(eq("framdriftsstatus"), eq(null), any())).thenReturn(
            framdriftsstatus()
        )
        Mockito.`when`(kodeverkService.hentKodeverk(eq("dokumenttype"), eq(null), any())).thenReturn(dokumentkategori())
    }

    @Test
    fun listen() {
        doNothing().`when`(behandlingService).lagreUtgaaendeJournalpostFraBrevutsending(any(), any())

        val payload = BrevutsendingUtfoertHendelse(
            behandlingId = 1234,
            journalpostId = "5678",
            metadata = BrevutsendingUtfoertMetadata(
                navCallId = UUID.randomUUID().toString()
            )
        )

        kafkaTemplate.send(BREVUTSENDING_UTFOERT_TOPIC, payload)

        // vent på at oppgavene i consumer blir fullført før vi gjennomfører testene
        consumer.latch.await(20000, TimeUnit.MILLISECONDS)
        assertThat(consumer.payload).isEqualTo(payload)
    }
}

@Primary
@Component
class BrevutsendingUtfoertHendelseConsumerForTest(
    behandlingService: BehandlingService
) : BrevutsendingUtfoertHendelseConsumer(behandlingService) {

    val latch = CountDownLatch(1)
    lateinit var payload: BrevutsendingUtfoertHendelse

    @KafkaListener(
        topics = [BREVUTSENDING_UTFOERT_TOPIC],
        containerFactory = "brevutsendingUtfoertHendelseListenerContainerFactory"
    )
    @Transactional
    override fun listen(record: BrevutsendingUtfoertHendelse) {
        super.listen(record)
        payload = record
        latch.countDown()
    }
}

