package no.nav.yrkesskade.saksbehandling.test

import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import no.nav.yrkesskade.saksbehandling.test.docker.KafkaDockerContainer
import no.nav.yrkesskade.saksbehandling.test.docker.PostgresDockerContainer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.TestPropertySourceUtils
import org.springframework.transaction.annotation.Transactional

@Transactional
@ActiveProfiles("integration")
@SpringBootTest
@DirtiesContext
@ContextConfiguration(initializers = [AbstractTest.DockerConfigInitializer::class])
@EnableMockOAuth2Server
abstract class AbstractTest {

    init {
        PostgresDockerContainer.container
        KafkaDockerContainer.container
    }

    @Autowired
    lateinit var server: MockOAuth2Server

    protected fun token(issuerId: String, subject: String, audience: String): String {
        return server.issueToken(
            issuerId = issuerId,
            clientId = "theclientid",
            tokenCallback = DefaultOAuth2TokenCallback(
                issuerId = issuerId,
                subject = subject,
                audience = listOf(audience),
                claims = emptyMap(),
                expiry = 3600L
            )
        ).serialize()
    }

    class DockerConfigInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(applicationContext: ConfigurableApplicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                applicationContext,
                "spring.datasource.url=" + PostgresDockerContainer.container.jdbcUrl,
                "spring.datasource.username=" + PostgresDockerContainer.container.username,
                "spring.datasource.password=" + PostgresDockerContainer.container.password,
                "spring.kafka.bootstrap-servers=" + KafkaDockerContainer.container.bootstrapServers
            )
        }
    }
}