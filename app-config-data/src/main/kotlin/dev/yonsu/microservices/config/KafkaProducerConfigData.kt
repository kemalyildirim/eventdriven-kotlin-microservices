package dev.yonsu.microservices.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "kafka-producer-config")
data class KafkaProducerConfigData(
    val keySerializerClass: String = "",
    val valueSerializerClass: String = "",
    val compressionType: String? = null,
    val acks: String = "1",
    val batchSize: Int = 1,
    val batchSizeBoostFactor: Int = 1,
    val lingerMs: Long = 0,
    val requestTimeoutMs: Long = 0,
    val retryCount: Int = 0
)
