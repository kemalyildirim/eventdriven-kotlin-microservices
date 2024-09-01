package dev.yonsu.microservices.common.config

import dev.yonsu.microservices.config.RetryConfigData
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.retry.backoff.ExponentialBackOffPolicy
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate

@Configuration
class RetryConfig(
    private val configData: RetryConfigData
) {

    @Bean
    fun retryTemplate() : RetryTemplate {
        val retryTemplate = RetryTemplate()
        val policy = ExponentialBackOffPolicy()
        policy.initialInterval = configData.initialIntervalMs
        policy.maxInterval = configData.maxIntervalMs
        policy.multiplier = configData.multiplier

        retryTemplate.setBackOffPolicy(policy)
        val simpleRetryPolicy = SimpleRetryPolicy()
        simpleRetryPolicy.maxAttempts = configData.maxAttempts
        retryTemplate.setRetryPolicy(simpleRetryPolicy)

        return retryTemplate
    }
}