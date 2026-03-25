package kuke.board.common.event.payload.comment

import kuke.board.common.event.EventPayload
import java.time.LocalDateTime

data class CommentCreatedEventPayload(
    val commentId: Long? = null,
    val content: String? = null,
    val path: String? = null,
    val parentId: Long? = null,
    val articleId: Long? = null,
    val writerId: Long? = null,
    val isTombstoned: Boolean? = null,
    val createdAt: LocalDateTime? = null,
    val modifiedAt: LocalDateTime? = null,
    val articleCommentCount: Long? = null,
) : EventPayload
