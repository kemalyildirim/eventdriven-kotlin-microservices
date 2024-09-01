package dev.yonsu.microservices.kafka.producer.service

import dev.yonsu.microservices.kafka.model.TwitterAvroModel
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import kotlinx.coroutines.future.await

@Service
class TwitterKafkaProducer(
    private val kafkaTemplate: KafkaTemplate<Long, TwitterAvroModel>
) : KafkaProducer<Long, TwitterAvroModel> {

    private val logger = LoggerFactory.getLogger(TwitterKafkaProducer::class.java)

    override suspend fun send(topicName: String, key: Long, message: TwitterAvroModel) {
        logger.info("Sending message='{}' to topic='{}'", message, topicName)

        runCatching {
            val result = kafkaTemplate.send(topicName, key, message).await()
            logger.debug(
                "Recieved new metadata. Topic: {} Partition: {} Offset: {} Timestamp: {}, at the time {}",
                result.recordMetadata.topic(),
                result.recordMetadata.partition(),
                result.recordMetadata.offset(),
                result.recordMetadata.timestamp(),
                System.nanoTime()
            )
        }.onFailure { ex ->
            logger.error("Failed to send data to Kafka", ex)

        }

    }

}