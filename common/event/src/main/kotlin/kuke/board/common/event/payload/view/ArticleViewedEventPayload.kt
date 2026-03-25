package kuke.board.common.event.payload.view

import kuke.board.common.event.EventPayload

data class ArticleViewedEventPayload(
    val articleId: Long? = null,
    val viewCount: Long? = null,
) : EventPayload
