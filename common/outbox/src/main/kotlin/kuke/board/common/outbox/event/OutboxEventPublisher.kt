package kuke.board.common.outbox.event

import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload
import kuke.board.common.event.EventType
import kuke.board.common.outbox.entity.Outbox
import kuke.board.common.outbox.enums.MessageRelayConstants
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class OutboxEventPublisher(
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    fun publish(
        eventType: EventType,
        payload: EventPayload,
        shardKey: Long,
    ) {
        val outbox = Outbox.create(
            eventType = eventType,
            payload = Event.of(
                type = eventType,
                payload = payload,
            ).toJson(),
            shardKey = shardKey % MessageRelayConstants.SHARD_COUNT,
        )
        applicationEventPublisher.publishEvent(
            OutboxEvent.of(
                outbox = outbox,
            )
        )
    }
}
