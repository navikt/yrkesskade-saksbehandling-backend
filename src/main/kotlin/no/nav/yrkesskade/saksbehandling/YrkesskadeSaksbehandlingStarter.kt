package no.nav.yrkesskade.saksbehandling

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class YrkesskadeSaksbehandlingStarter

fun main(args: Array<String>) {
    runApplication<YrkesskadeSaksbehandlingStarter>(*args)
}