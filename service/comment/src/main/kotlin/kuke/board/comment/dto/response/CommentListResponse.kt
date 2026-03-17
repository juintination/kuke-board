package kuke.board.comment.dto.response

import kuke.board.comment.entity.Comment
import java.time.LocalDateTime

data class CommentListResponse(
    val id: Long,
    val parentId: Long?,
    val articleId: Long,
    val writerId: Long,
    val content: String,
    val isTombstoned: Boolean,
    val hasChildren: Boolean,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
) {
    companion object {
        fun from(
            comment: Comment,
            hasChildren: Boolean,
        ): CommentListResponse {
            return CommentListResponse(
                id = comment.id!!,
                parentId = comment.parentId,
                articleId = comment.articleId,
                writerId = comment.writerId,
                content = comment.content,
                isTombstoned = comment.isTombstoned(),
                hasChildren = hasChildren,
                createdAt = comment.createdAt,
                modifiedAt = comment.modifiedAt,
            )
        }
    }
}
