package dev.yonsu.microservices.kafka.admin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KafkaAdminApplication

fun main(args: Array<String>) {
    runApplication<KafkaAdminApplication>(*args)
}
