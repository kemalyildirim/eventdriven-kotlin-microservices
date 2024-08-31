package dev.yonsu.microservices.xtokafkaservice.runner

import dev.yonsu.microservices.config.XToKafkaConfigData
import dev.yonsu.microservices.xtokafkaservice.listener.XKafkaStatusListener
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import twitter4j.TwitterObjectFactory
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random
import kotlin.random.nextInt


@Component
@ConditionalOnProperty(name = ["x-to-kafka-service.enable-mock-tweets"], havingValue = "true")
class MockKafkaStreamRunner(
    val configData: XToKafkaConfigData, val statusListener: XKafkaStatusListener
) : StreamRunner {
    private val logger = LoggerFactory.getLogger(MockKafkaStreamRunner::class.java)
    val RANDOM = Random(5)
    val WORDS = listOf(
        "Lorem",
        "ipsum",
        "dolor",
        "sit",
        "amet,",
        "consectetur",
        "adipiscing",
        "elit,",
        "sed",
        "do",
        "eiusmod",
        "tempor",
        "incididunt",
        "ut",
        "labore",
        "et",
        "dolore",
        "magna",
        "aliqua.",
        "Ut",
        "enim",
        "ad",
        "minim",
        "veniam,",
        "quis",
        "nostrud",
        "exercitation",
        "ullamco",
        "laboris",
        "nisi",
        "ut",
        "aliquip",
        "ex",
        "ea",
        "commodo",
        "consequat.",
        "Duis",
        "aute",
        "irure",
        "dolor",
        "in",
        "reprehenderit",
        "in",
        "voluptate",
        "velit",
        "esse",
        "cillum",
        "dolore",
        "eu",
        "fugiat",
        "nulla",
        "pariatur.",
        "Excepteur",
        "sint",
        "occaecat",
        "cupidatat",
        "non",
        "proident,",
        "sunt",
        "in",
        "culpa",
        "qui",
        "officia",
        "deserunt",
        "mollit",
        "anim",
        "id",
        "est",
        "laborum"
    )

    var tweetAsRawJson = """
        {
            "created_at": "{0}",
            "id": {1},
            "text": "{2}",
            "user": { "id": {3} }
        }
    """.trimIndent()

    val TWITTER_STATUS_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy"

    private fun sleep(duration: Long) {
        logger.info("Sleeping for {} ms", duration)
        Thread.sleep(duration)
    }

    private fun getFormattedTweet(keywords: Array<String>, minLen: Int, maxLen: Int): String {
        val params: Array<String> = arrayOf(
            ZonedDateTime.now().format(DateTimeFormatter.ofPattern(TWITTER_STATUS_DATE_FORMAT, Locale.ENGLISH)).toString(),
            ThreadLocalRandom.current().nextLong().toString(),
            getRandomTweetContent(keywords, minLen, maxLen),
            ThreadLocalRandom.current().nextLong().toString()
        )
        var tweet = tweetAsRawJson
        for (i in 0..params.size - 1) {
            tweet = tweet.replace("{${i}}", params[i])
        }
        return tweet
    }

    private fun getRandomTweetContent(keywords: Array<String>, minLen: Int, maxLen: Int): String {
        var tweet = StringBuilder()
        val tweetLength = RANDOM.nextInt(minLen + 1..maxLen)
        for (i in 0..tweetLength) {
            tweet.append(WORDS[RANDOM.nextInt(WORDS.size)]).append(" ")
            if (i == tweetLength / 2) {
                tweet.append(keywords[RANDOM.nextInt(keywords.size)]).append(" ")
            }
        }
        return tweet.toString().trimIndent()
    }

    override fun start() {
        logger.info("Starting mock filtering stream with keywords: {}", configData.xKeywords)
        val keywords = configData.xKeywords.toTypedArray()
        streamMockTweets(keywords)
    }

    private fun streamMockTweets(keywords: Array<String>) {
        Executors.newSingleThreadExecutor().submit {
            while (true) {
                val formattedTweet =
                    getFormattedTweet(keywords, configData.mockMinTweetLength, configData.mockMaxTweetLength)
                val status = TwitterObjectFactory.createStatus(formattedTweet)
                statusListener.onStatus(status)
                sleep(configData.mockSleepMs.toLong())
            }
        }
    }
}