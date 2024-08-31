package dev.yonsu.microservices.xtokafkaservice

import dev.yonsu.microservices.xtokafkaservice.runner.MockKafkaStreamRunner
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["dev.yonsu.microservices"])
class Application(
    private val streamRunner: MockKafkaStreamRunner
) : CommandLineRunner {

    val logger: Logger = LoggerFactory.getLogger(Application::class.java)
    override fun run(vararg args: String?) {
        logger.info("app starts")
        streamRunner.start()
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
