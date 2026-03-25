package kuke.board.hotarticle.consumer

import io.github.oshai.kotlinlogging.KotlinLogging
import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload
import kuke.board.common.event.Topics
import kuke.board.hotarticle.service.HotArticleService
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class HotArticleEventConsumer(
    private val hotArticleService: HotArticleService,
) {

    private val log = KotlinLogging.logger {}

    @KafkaListener(
        topics = [
            Topics.KUKE_BOARD_ARTICLE,
            Topics.KUKE_BOARD_COMMENT,
            Topics.KUKE_BOARD_LIKE,
            Topics.KUKE_BOARD_VIEW,
        ]
    )
    fun listen(
        message: String,
        ack: Acknowledgment,
    ) {
        log.info { "[HotArticleEventConsumer.listen] received message=$message" }

        val event: Event<EventPayload> = Event.fromJson(message)
            ?: run {
                log.warn { "Failed to deserialize event: $message" }
                ack.acknowledge()
                return
            }

        log.info { "[HotArticleEventConsumer.listen] deserialized event=$event" }
        hotArticleService.handleEvent(
            event = event,
        )

        ack.acknowledge()
    }
}
