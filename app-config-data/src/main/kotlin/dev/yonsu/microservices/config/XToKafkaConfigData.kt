package dev.yonsu.microservices.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "x-to-kafka-service")
data class XToKafkaConfigData(
    var xKeywords: List<String> = listOf(),
    var enableV2Tweets: Boolean = false,
    var enableMockTweets: Boolean = false,
    var mockMinTweetLength: Int = 5,
    var mockMaxTweetLength: Int = 10,
    var mockSleepMs: Int = 5000
)
