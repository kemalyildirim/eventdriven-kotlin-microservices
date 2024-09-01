package dev.yonsu.microservices.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "kafka-config")
data class KafkaConfigData(
    val bootstrapServers: String = "",
    val schemaRegistryUrlKey: String = "",
    val schemaRegistryUrl: String = "",
    val topicName: String = "",
    val topicNamesToCreate: List<String> = listOf(),
    val numOfPartitions: Int = 0,
    val replicationFactor: Short = 0
    ) {
}