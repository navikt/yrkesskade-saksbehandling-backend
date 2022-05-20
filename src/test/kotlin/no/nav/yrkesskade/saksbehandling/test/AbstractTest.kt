package no.nav.yrkesskade.saksbehandling.test

import no.nav.yrkesskade.saksbehandling.test.docker.KafkaDockerContainer
import no.nav.yrkesskade.saksbehandling.test.docker.PostgresDockerContainer
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.support.TestPropertySourceUtils

const val TOPIC_NAME = "test-topic"

@ActiveProfiles("integration")
@ExtendWith(SpringExtension::class)
@SpringBootTest
@DirtiesContext
@ContextConfiguration(initializers = [AbstractTest.DockerConfigInitializer::class])
abstract class AbstractTest {

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
                "spring.kafka.bootstrap-servers=" + KafkaDockerContainer.container.bootstrapServers,
                "kafka.topic.skademelding-innsendt=" + TOPIC_NAME
            );
        }
    }

}