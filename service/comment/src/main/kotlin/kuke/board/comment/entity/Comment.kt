package kuke.board.comment.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import kuke.board.comment.dto.request.CommentUpdateRequest
import kuke.board.jpa.entity.BaseEntity
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@Entity
@Table(name = "comment")
@SQLRestriction("deleted_at is null")
@SQLDelete(sql = "update comment set deleted_at = now() where id = ?")
class Comment private constructor(

    @Id
    @Column(columnDefinition = "BIGINT UNSIGNED")
    val id: Long,

    @Column(name = "parent_id", columnDefinition = "BIGINT UNSIGNED")
    val parentId: Long?,

    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    val articleId: Long,

    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    val writerId: Long,

    @Column(length = 1000, nullable = false)
    var content: String,
) : BaseEntity() {
    companion object {
        fun create(
            id: Long,
            parentId: Long?,
            articleId: Long,
            writerId: Long,
            content: String,
        ): Comment {
            return Comment(
                id = id,
                parentId = parentId,
                articleId = articleId,
                writerId = writerId,
                content = content,
            )
        }
    }

    fun update(
        request: CommentUpdateRequest,
    ) {
        this.content = request.content
    }

    fun isRoot(): Boolean = parentId == null

    fun isDeleted(): Boolean = deletedAt != null
}
