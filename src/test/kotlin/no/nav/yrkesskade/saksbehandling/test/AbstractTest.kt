package no.nav.yrkesskade.saksbehandling.test

import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import no.nav.yrkesskade.saksbehandling.test.docker.KafkaDockerContainer
import no.nav.yrkesskade.saksbehandling.test.docker.PostgresDockerContainer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.TestPropertySourceUtils
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

private const val KAFKA_CONTAINER_IMAGE_NAME = "confluentinc/cp-kafka:7.0.1"

private const val POSTGRES_IMAGE_NAME = "postgres:12"

@Transactional(propagation = Propagation.REQUIRED)
//@Testcontainers
@ActiveProfiles("integration")
@SpringBootTest
@DirtiesContext
@ContextConfiguration(initializers = [AbstractTest.DockerConfigInitializer::class])
@EnableMockOAuth2Server
@Rollback
abstract class AbstractTest {

//    @Container
//    val postgresDockerContainer = PostgreSQLContainer(POSTGRES_IMAGE_NAME).apply {
//        withReuse(true)
//    }
//
//    @Container
//    val kafkaDockerContainer = KafkaContainer(DockerImageName.parse(KAFKA_CONTAINER_IMAGE_NAME)).apply {
//        withReuse(true)
//    }

    init {
        PostgresDockerContainer.container
        KafkaDockerContainer.container
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