package kuke.board.common.event.payload.article

import kuke.board.common.event.EventPayload
import java.time.LocalDateTime

data class ArticleDeletedEventPayload(
    val articleId: Long? = null,
    val title: String? = null,
    val content: String? = null,
    val boardId: Long? = null,
    val writerId: Long? = null,
    val createdAt: LocalDateTime? = null,
    val modifiedAt: LocalDateTime? = null,
) : EventPayload
