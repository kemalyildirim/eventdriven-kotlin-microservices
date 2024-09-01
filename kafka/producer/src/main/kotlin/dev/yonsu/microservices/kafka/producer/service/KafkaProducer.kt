package dev.yonsu.microservices.kafka.producer.service

import org.apache.avro.specific.SpecificRecordBase
import java.io.Serializable

fun interface KafkaProducer<K : Serializable, V : SpecificRecordBase> {
    suspend fun send(topicName: String, key: K, message: V)
}