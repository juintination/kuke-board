package kuke.board.comment.dto.response

import kuke.board.comment.entity.Comment
import java.time.LocalDateTime

data class CommentResponse(
    val id: Long,
    val path: String,
    val articleId: Long,
    val writerId: Long,
    val parentId: Long?,
    val content: String,
    val isTombstoned: Boolean,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
) {
    companion object {
        fun from(
            comment: Comment,
        ): CommentResponse {
            return CommentResponse(
                id = comment.id,
                path = comment.path.path,
                articleId = comment.articleId,
                writerId = comment.writerId,
                parentId = comment.parentId,
                content = comment.content,
                isTombstoned = comment.isTombstoned(),
                createdAt = comment.createdAt,
                modifiedAt = comment.modifiedAt,
            )
        }
    }
}
