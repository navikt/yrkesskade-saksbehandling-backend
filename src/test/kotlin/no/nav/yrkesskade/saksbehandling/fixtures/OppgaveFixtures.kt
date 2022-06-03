import java.nio.file.Files
import java.nio.file.Path

fun oppgaveMedBehandlesAvApplikasjon(): String {
    return Files.readString(Path.of("src/test/resources/oppgaver/enkel-oppgave.json"))
}

fun oppgaveUtenBehandlesAvApplikasjon(): String {
    return Files.readString(Path.of("src/test/resources/oppgaver/enkel-oppgave-uten-behandlesavapp.json"))
}

fun oppgaveUtenBehandlesAvApplikasjonAlt2(): String {
    return Files.readString(Path.of("src/test/resources/oppgaver/enkel-oppgave-uten-behandlesavapp-2.json"))
}

fun oppgaveUtenBehandlesAvApplikasjonAnnetFnr(): String {
    return Files.readString(Path.of("src/test/resources/oppgaver/enkel-oppgave-uten-behandlesavapp-3.json"))
}