package no.nav.yrkesskade.saksbehandling.client.oppgave

import net.datafaker.Faker
import java.time.LocalDate
import java.time.OffsetDateTime

class OppgaveFactory {

    companion object {
        private val faker = Faker()

        fun enOppgave(): Oppgave {
            return Oppgave(
                id = faker.number().numberBetween(0L, 100L),
                tildeltEnhetsnr = faker.regexify("[0-9]{4}"),
                aktivDato = LocalDate.now(),
                endretAvEnhetsnr = faker.regexify("[0-9]{4}"),
                status = faker.options().nextElement(Status.values()),
                tema = "YRK",
                versjon = faker.number().positive(),
                opprettetAv = faker.funnyName().name(),
                prioritet = faker.options().nextElement(Prioritet.values()),
                opprettetTidspunkt = OffsetDateTime.now(),
                fristFerdigstillelse = LocalDate.now().plusDays(10),
                oppgavetype = "JFR"
            )
        }
    }
}