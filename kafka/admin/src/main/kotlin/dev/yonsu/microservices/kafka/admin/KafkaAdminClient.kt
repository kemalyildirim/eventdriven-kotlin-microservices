package dev.yonsu.microservices.kafka.admin

import dev.yonsu.microservices.config.KafkaConfigData
import dev.yonsu.microservices.config.RetryConfigData
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.CreateTopicsResult
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.admin.TopicListing
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.retry.RetryContext
import org.springframework.retry.support.RetryTemplate
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.lang.Exception


@Component
class KafkaAdminClient(
    private val kafkaConfigData: KafkaConfigData,
    private val adminClient: AdminClient,
    private val retryConfigData: RetryConfigData,
    private val retryTemplate: RetryTemplate,
    private val webClient: WebClient
) {

    private val logger = LoggerFactory.getLogger(KafkaAdminClient::class.java)

    fun createTopics() {
        val createTopicsResult: CreateTopicsResult
        try {
            createTopicsResult = retryTemplate.execute<CreateTopicsResult, Throwable> {
                doCreateTopics(it)
            }
        } catch (t: Throwable) {
            throw RuntimeException("Max number of retry reached for creating Kafka topic(s)", t)
        }
        checkTopicsCreated()
    }

    private fun checkTopicsCreated() {
        var topics = getTopics()
        var retryCount = 1
        var sleepTime = retryConfigData.sleepTimeMs
        for (topic in kafkaConfigData.topicNamesToCreate) {
            while (!isTopicCreated(topics, topic)) {
                checkMaxRetry(retryCount++, retryConfigData.maxAttempts)
                Thread.sleep(sleepTime)
                sleepTime *= (sleepTime.toDouble() * retryConfigData.multiplier).toLong()
                topics = getTopics()
            }
        }
    }

    private fun schemaRegistryStatus(): HttpStatusCode? {
        try {
            return webClient
                .get()
                .uri(kafkaConfigData.schemaRegistryUrl)
                .exchangeToMono {
                    Mono.just(it.statusCode())
                }
                .block()
        } catch (e: Exception) {
            return HttpStatus.SERVICE_UNAVAILABLE
        }
    }

    fun checkSchemaRegistry() {
        var retryCount = 1
        var sleepTime = retryConfigData.sleepTimeMs
        for (topic in kafkaConfigData.topicNamesToCreate) {
            while (schemaRegistryStatus()?.is2xxSuccessful == true) {
                checkMaxRetry(retryCount++, retryConfigData.maxAttempts)
                Thread.sleep(sleepTime)
                sleepTime *= (sleepTime.toDouble() * retryConfigData.multiplier).toLong()
            }
        }
    }

    private fun isTopicCreated(topics: Collection<TopicListing>, topic: String): Boolean {
        return topics.stream().anyMatch { it.name() == topic }
    }

    private fun checkMaxRetry(retryCount: Int, maxAttempts: Int) {
        if (retryCount > maxAttempts) {
            throw RuntimeException("Reached max number of retries for reading Kafka topic(s)")
        }
    }

    fun doCreateTopics(retryContext: RetryContext): CreateTopicsResult {
        logger.info("Creating topics: {} attempt: {}", kafkaConfigData.topicNamesToCreate, retryContext.retryCount)
        val kafkaTopics = kafkaConfigData.topicNamesToCreate.map {
            NewTopic(it.trim(), kafkaConfigData.numOfPartitions, kafkaConfigData.replicationFactor)
        }
        return adminClient.createTopics(kafkaTopics)
    }

    private fun getTopics(): Collection<TopicListing> {
        try {

            return retryTemplate.execute<Collection<TopicListing>, Exception> {
                doGetTopics(it)
            }
        } catch (t: Throwable) {
            throw RuntimeException("Max number of retry reached for reading Kafka topic(s)", t)
        }
    }

    private fun doGetTopics(retryContext: RetryContext): Collection<TopicListing> {
        logger.info("Reading Kafka topic {}, attempt [}", kafkaConfigData.topicNamesToCreate, retryContext.retryCount)
        val topics = adminClient.listTopics().listings().get()
        topics.forEach { logger.debug("Topic with name: {}", it) }
        return topics
    }

}