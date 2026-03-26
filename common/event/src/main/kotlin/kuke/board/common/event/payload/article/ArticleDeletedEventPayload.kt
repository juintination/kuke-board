package kuke.board.common.event.payload.article

import kuke.board.common.event.EventPayload
import java.time.LocalDateTime

data class ArticleDeletedEventPayload(
    val articleId: Long,
    val title: String,
    val content: String,
    val boardId: Long,
    val writerId: Long,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
) : EventPayload
