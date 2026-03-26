package kuke.board.common.event.payload.view

import kuke.board.common.event.EventPayload

data class ArticleViewedEventPayload(
    val articleId: Long,
    val articleViewCount: Long,
) : EventPayload
