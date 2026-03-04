package kuke.board.comment.entity

import jakarta.persistence.*
import kuke.board.comment.dto.request.CommentUpdateRequest
import kuke.board.jpa.entity.BaseEntity
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

@Entity
@Table(name = "comment")
@SQLRestriction("deleted_at is null")
@SQLDelete(sql = "update comment set deleted_at = now() where id = ?")
class Comment private constructor(

    @Id
    @Column(columnDefinition = "BIGINT UNSIGNED")
    val id: Long,

    @Embedded
    val path: CommentPath,

    @Column(name = "parent_id", columnDefinition = "BIGINT UNSIGNED")
    val parentId: Long?,

    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    val articleId: Long,

    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    val writerId: Long,

    @Column(length = 1000, nullable = false)
    var content: String,

    @Column(name = "tombstoned_at", columnDefinition = "DATETIME(6)")
    var tombstonedAt: LocalDateTime? = null,
) : BaseEntity() {
    companion object {
        fun create(
            id: Long,
            parentId: Long?,
            articleId: Long,
            writerId: Long,
            content: String,
            path: CommentPath,
        ): Comment {
            return Comment(
                id = id,
                parentId = parentId,
                articleId = articleId,
                writerId = writerId,
                content = content,
                path = path,
            )
        }
    }

    fun update(
        request: CommentUpdateRequest,
    ) {
        if (isTombstoned()) {
            throw IllegalStateException("삭제된 댓글은 수정할 수 없습니다. commentId: $id")
        }
        this.content = request.content
    }

    fun isTombstoned(): Boolean = tombstonedAt != null

    fun tombstone() {
        if (isTombstoned()) return
        tombstonedAt = LocalDateTime.now()
    }
}
