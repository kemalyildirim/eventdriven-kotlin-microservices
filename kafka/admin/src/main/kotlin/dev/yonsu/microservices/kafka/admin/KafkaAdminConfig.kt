package dev.yonsu.microservices.kafka.admin

import dev.yonsu.microservices.config.KafkaConfigData
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.admin.AdminClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.retry.annotation.EnableRetry

@Configuration
@EnableRetry
class KafkaAdminConfig(
    private val kafkaConfigData: KafkaConfigData
) {

    @Bean
    fun adminClient(): AdminClient {
        return AdminClient.create(
            mapOf(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG to kafkaConfigData.bootstrapServers)
        )
    }
}
