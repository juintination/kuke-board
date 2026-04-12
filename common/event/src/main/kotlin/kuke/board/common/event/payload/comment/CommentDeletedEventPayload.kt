package kuke.board.common.event.payload.comment

import kuke.board.common.event.EventPayload
import java.time.LocalDateTime

data class CommentDeletedEventPayload(
    val commentId: Long,
    val content: String,
    val parentId: Long?,
    val path: String,
    val articleId: Long,
    val writerId: Long,
    val isTombstoned: Boolean,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
    val articleCommentCount: Long,
) : EventPayload
