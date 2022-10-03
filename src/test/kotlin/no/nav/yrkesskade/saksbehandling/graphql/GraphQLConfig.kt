package no.nav.yrkesskade.saksbehandling.graphql

import no.nav.yrkesskade.saksbehandling.client.bigquery.BigQueryClient
import no.nav.yrkesskade.saksbehandling.client.dokarkiv.DokarkivClient
import no.nav.yrkesskade.saksbehandling.client.oppgave.OppgaveClient
import no.nav.yrkesskade.saksbehandling.graphql.client.pdl.PdlClient
import no.nav.yrkesskade.saksbehandling.graphql.client.saf.SafClient
import no.nav.yrkesskade.saksbehandling.repository.BehandlingRepository
import no.nav.yrkesskade.saksbehandling.security.AutentisertBruker
import no.nav.yrkesskade.saksbehandling.service.BehandlingService
import no.nav.yrkesskade.saksbehandling.service.BehandlingsoverfoeringLogService
import no.nav.yrkesskade.saksbehandling.service.PersonService
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.transaction.PlatformTransactionManager


class GraphQLConfig {

    @MockBean
    lateinit var behandlingRepository: BehandlingRepository

    @MockBean
    lateinit var behandlingsoverfoeringLogService: BehandlingsoverfoeringLogService

    @MockBean
    lateinit var autentisertBruker: AutentisertBruker

    @MockBean
    lateinit var transactionManager: PlatformTransactionManager

    @MockBean
    lateinit var dokarkivClient: DokarkivClient

    @MockBean
    lateinit var safClient: SafClient

    @MockBean
    lateinit var pdlClient: PdlClient

    @MockBean
    lateinit var oppgaveClient: OppgaveClient

    @MockBean
    lateinit var bigQueryClient: BigQueryClient

    @Bean
    fun behandlingService(): BehandlingService {
        return BehandlingService(
            autentisertBruker,
            behandlingRepository,
            behandlingsoverfoeringLogService,
            dokarkivClient,
            oppgaveClient,
            pdlClient,
            safClient,
            bigQueryClient,
            "Kompys",
            "Kompys"
        )
    }

    @Bean
    fun personService(): PersonService {
        return PersonService(pdlClient)
    }
}