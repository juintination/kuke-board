package kuke.board.articleread.service.event

import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload

interface EventHandler<T : EventPayload> {
    fun handle(event: Event<T>)
    fun supports(event: Event<T>): Boolean
}
