package no.nav.yrkesskade.saksbehandling.skademelding.hendelse

import no.nav.yrkesskade.model.SkademeldingInnsendtHendelse
import no.nav.yrkesskade.saksbehandling.skademelding.service.SkademeldingService
import no.nav.yrkesskade.saksbehandling.util.getLogger
import no.nav.yrkesskade.saksbehandling.util.getSecureLogger
import no.nav.yrkesskade.saksbehandling.util.kallMetodeMedCallId
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import java.util.concurrent.CountDownLatch
import javax.transaction.Transactional


@Component
class SkademeldingInnsendtHendelseConsumer(private val skademeldingService: SkademeldingService) {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    val latch = CountDownLatch(1)
    var payload: SkademeldingInnsendtHendelse? = null

    @KafkaListener(
        id = "skademelding-innsendt",
        topics = ["\${kafka.topic.skademelding-innsendt}"],
        containerFactory = "skademeldingInnsendtHendelseListenerContainerFactory",
        idIsGroup = false
    )
    @Transactional
    fun listen(@Payload record: SkademeldingInnsendtHendelse) {
        kallMetodeMedCallId(record.metadata.navCallId) {
            val lagretSkademelding = skademeldingService.lagreSkademelding(skademeldingInnsendtHendelse = record)
            payload = record
            latch.countDown()
            logger.info("Skademelding lagret i database med id: ${lagretSkademelding.skademeldingId}")
            secureLogger.info("Skademelding [$record] lagret i database: [$lagretSkademelding]")
        }
    }
}