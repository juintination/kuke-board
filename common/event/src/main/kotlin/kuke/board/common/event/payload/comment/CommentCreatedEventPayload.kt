package kuke.board.common.event.payload.comment

import kuke.board.common.event.EventPayload
import java.time.LocalDateTime

data class CommentCreatedEventPayload(
    val commentId: Long,
    val content: String,
    val path: String,
    val parentId: Long,
    val articleId: Long,
    val writerId: Long,
    val isTombstoned: Boolean,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
    val articleCommentCount: Long,
) : EventPayload
