package no.nav.yrkesskade.saksbehandling.graphql

import no.nav.yrkesskade.saksbehandling.repository.BehandlingRepository
import no.nav.yrkesskade.saksbehandling.security.AutentisertBruker
import no.nav.yrkesskade.saksbehandling.service.BehandlingService
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

class GraphQLConfig {

    @MockBean
    lateinit var behandlingRepository: BehandlingRepository

    @MockBean
    lateinit var autentisertBruker: AutentisertBruker

    @Bean
    fun behandlingService(): BehandlingService {
        return BehandlingService(autentisertBruker, behandlingRepository)
    }
}