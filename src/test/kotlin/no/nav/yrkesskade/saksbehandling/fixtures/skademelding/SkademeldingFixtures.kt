package no.nav.yrkesskade.saksbehandling.fixtures.skademelding

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.yrkesskade.skademelding.model.Skademelding
import java.nio.file.Files
import java.nio.file.Path


fun skademeldingMedTidspunktJson(): String {
    return Files.readString(Path.of("src/test/resources/skademeldinger/skademelding_med_tidspunkt.json"))
}

fun skademeldingMedPeriodeFraDatoSammeSomTilDatoJson(): String {
    return Files.readString(Path.of("src/test/resources/skademeldinger/fradatoSammeSomTilDato.json"))
}

fun skademeldingMedTidspunkt(): Skademelding {
    return jacksonObjectMapper().registerModule(JavaTimeModule()).readValue(skademeldingMedTidspunktJson(), Skademelding::class.java)
}

