package dev.yonsu.xtokafkaservice.listener

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import twitter4j.StallWarning
import twitter4j.Status
import twitter4j.StatusDeletionNotice
import twitter4j.StatusListener
import java.lang.Exception

@Component
class XKafkaStatusListener : StatusListener {
    val logger = LoggerFactory.getLogger(XKafkaStatusListener::class.java)

    override fun onException(p0: Exception?) {
        logger.error("ERROR OCCURED: ", p0)
    }

    override fun onStatus(p0: Status?) {
        logger.info("[onStatus] status text: {}", p0?.text)
    }

    override fun onDeletionNotice(p0: StatusDeletionNotice?) {
        TODO("Not yet implemented")
    }

    override fun onTrackLimitationNotice(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun onScrubGeo(p0: Long, p1: Long) {
        TODO("Not yet implemented")
    }

    override fun onStallWarning(p0: StallWarning?) {
        TODO("Not yet implemented")
    }
}