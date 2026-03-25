package kuke.board.common.outbox.event

import kuke.board.common.event.EventType
import kuke.board.common.outbox.entity.Outbox

class OutboxEvent private constructor(
    val eventType: EventType,
    val payload: String,
    val shardKey: Long,
) {
    companion object {
        fun of(
            outbox: Outbox,
        ) = OutboxEvent(
            eventType = outbox.eventType,
            payload = outbox.payload,
            shardKey = outbox.shardKey,
        )
    }
}
