package no.nav.yrkesskade.saksbehandling.config

import org.springframework.retry.backoff.ExponentialBackOffPolicy
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate

private const val ANTALL_RETRIES = 10
private const val ETT_SEKUND = 1000L

abstract class AbstractKafkaConfig {

    fun retryTemplate() = RetryTemplate().apply {
        this.setBackOffPolicy(ExponentialBackOffPolicy().apply {
            this.initialInterval = ETT_SEKUND
        })
        this.setRetryPolicy(SimpleRetryPolicy(ANTALL_RETRIES))
    }
}