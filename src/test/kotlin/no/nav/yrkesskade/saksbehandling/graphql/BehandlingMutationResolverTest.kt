package no.nav.yrkesskade.saksbehandling.graphql

import com.graphql.spring.boot.test.GraphQLTest
import com.graphql.spring.boot.test.GraphQLTestTemplate
import no.nav.yrkesskade.saksbehandling.config.GraphQLScalarsConfig
import no.nav.yrkesskade.saksbehandling.hendelser.oppgave.model.Oppgavestatuskategori
import no.nav.yrkesskade.saksbehandling.model.*
import no.nav.yrkesskade.saksbehandling.repository.BehandlingRepository
import no.nav.yrkesskade.saksbehandling.service.BehandlingService
import no.nav.yrkesskade.saksbehandling.test.AbstractTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import java.time.Instant
import java.time.LocalDate
import java.util.*

@Import(value = [GraphQLScalarsConfig::class, GraphQLConfig::class])
@GraphQLTest
class BehandlingMutationResolverTest : AbstractTest() {

    @Autowired
    lateinit var graphQLTestTemplate: GraphQLTestTemplate

    @Autowired
    lateinit var behandlingService: BehandlingService

    @Autowired
    lateinit var behandlingRepository: BehandlingRepository

    private val aapenBehandling: BehandlingEntity = BehandlingEntity(
        behandlingId = 1,
        behandlingstema = "test",
        status = Behandlingsstatus.IKKE_PAABEGYNT,
        statuskategori = Oppgavestatuskategori.AAPEN,
        aktivDato = LocalDate.now(),
        fristFerdigstillelse = LocalDate.now().plusDays(10),
        opprettetAv = "test",
        opprettetTidspunkt = Instant.now(),
        behandlingResultater = emptyList(),
        behandlingsansvarligIdent = null,
        ansvarligEnhet = null,
        oppgaveId = "test",
        oppgavetype = "test-oppgave",
        endretAv = "test",
        sak = SakEntity(
            sakId = 1,
            saksstatus = Saksstatus.AAPEN,
            opprettetTidspunkt = Instant.now(),
            opprettetAv = "test",
            sakstype = Sakstype.YRKESSYKDOM,
            aktoerId = "test",
            brukerIdentifikator = "test",
            brukerFornavn = "Test",
            brukerEtternavn = "Testesen",
            behandlinger = emptyList(),
            brukerMellomnavn = null
        ),
        dokumentMetaer = emptyList()
    )

    @Test
    fun `overta behandling - behandling eksisterer`() {
        Mockito.`when`(behandlingRepository.findById(any())).thenReturn(Optional.of(aapenBehandling))

        val response = graphQLTestTemplate.postForResource("graphql/overta-behandling.graphql")
        assertThat(response.statusCode.is2xxSuccessful).isTrue
        assertThat(response.get("$.data.overtaBehandling.status")).isEqualTo(Behandlingsstatus.UNDER_BEHANDLING.name)
    }

    @Test
    fun `overta behandling - behandling eksisterer ikke`() {
        Mockito.`when`(behandlingRepository.findById(any())).thenReturn(null)

        val response = graphQLTestTemplate.postForResource("graphql/overta-behandling.graphql")
        assertThat(response.statusCode.is2xxSuccessful).isTrue
        assertThat(response.get("$.errors.length()")).isEqualTo("1")
        assertThat(response.get("$.data.overtaBehandling")).isNull()
    }
}