package dev.yonsu.microservices.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "retry-config")
data class RetryConfigData(
    val initialIntervalMs: Long = 1000,
    val maxIntervalMs: Long = 2000,
    val multiplier: Double = 1.0,
    val maxAttempts: Int = 3,
    val sleepTimeMs: Long = 500
)
