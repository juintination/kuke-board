package kuke.board.comment.entity

import io.hypersistence.utils.hibernate.id.Tsid
import jakarta.persistence.*
import kuke.board.comment.dto.request.CommentUpdateRequest
import kuke.board.jpa.entity.BaseEntity
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

@Entity
@Table(
    name = "comment",
    indexes = [
        Index(
            name = "idx_comment_article_id_parent_id_deleted_at_id",
            columnList = "article_id, parent_id, deleted_at, id"
        )
    ]
)
@SQLRestriction("deleted_at is null")
@SQLDelete(sql = "update comment set deleted_at = now() where id = ?")
class Comment private constructor(

    @Id
    @Tsid
    @Column(columnDefinition = "BIGINT UNSIGNED")
    val id: Long? = null,

    @Embedded
    val path: CommentPath,

    @Column(columnDefinition = "BIGINT UNSIGNED")
    val parentId: Long?,

    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    val articleId: Long,

    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    val writerId: Long,

    @Column(length = 1000, nullable = false)
    var content: String,

    @Column(columnDefinition = "DATETIME(6)")
    var tombstonedAt: LocalDateTime? = null,
) : BaseEntity() {
    companion object {
        fun create(
            parentId: Long?,
            articleId: Long,
            writerId: Long,
            content: String,
            path: CommentPath,
        ) = Comment(
            parentId = parentId,
            articleId = articleId,
            writerId = writerId,
            content = content,
            path = path,
        )
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
