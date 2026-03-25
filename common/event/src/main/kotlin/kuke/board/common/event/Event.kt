package kuke.board.common.event

import io.hypersistence.tsid.TSID
import kuke.board.common.serialization.DataSerializer

data class Event<T : EventPayload>(
    val eventId: Long,
    val type: EventType,
    val payload: T,
) {

    fun toJson(): String {
        return DataSerializer.toJson(
            obj = this,
        ) ?: throw IllegalStateException("Failed to serialize Event: $this")
    }

    companion object {
        fun of(
            type: EventType,
            payload: EventPayload,
        ): Event<EventPayload> {
            return Event(
                eventId = TSID.fast().toLong(),
                type = type,
                payload = payload,
            )
        }

        fun fromJson(
            json: String,
        ): Event<EventPayload>? {
            val eventRaw = DataSerializer.fromJson<EventRaw>(
                data = json,
            ) ?: return null

            val type = EventType.from(
                type = eventRaw.type,
            ) ?: return null

            val payload = DataSerializer.convert(
                data = eventRaw.payload,
                clazz = type.payloadClass,
            )

            return Event(
                eventId = eventRaw.eventId,
                type = type,
                payload = payload,
            )
        }
    }

    private data class EventRaw(
        val eventId: Long,
        val type: String,
        val payload: Any,
    )
}
