package kuke.board.like.entity

import io.hypersistence.utils.hibernate.id.Tsid
import jakarta.persistence.*
import kuke.board.common.jpa.entity.BaseEntity
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

@Entity
@Table(
    name = "article_like",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_article_like_article_id_user_id",
            columnNames = ["article_id", "user_id"]
        )
    ]
)
@SQLRestriction("deleted_at is null")
@SQLDelete(sql = "update article_like set deleted_at = now() where id = ?")
class ArticleLike private constructor(

    @Id
    @Tsid
    @Column(columnDefinition = "BIGINT UNSIGNED")
    val id: Long? = null,

    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    val articleId: Long,

    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    val userId: Long,

    @Column(columnDefinition = "DATETIME(6)")
    var tombstonedAt: LocalDateTime? = null,
) : BaseEntity() {
    companion object {
        fun create(
            articleId: Long,
            userId: Long,
        ) = ArticleLike(
            articleId = articleId,
            userId = userId,
        )
    }

    fun isTombstoned(): Boolean = tombstonedAt != null

    fun tombstone() {
        if (isTombstoned()) {
            return
        }
        tombstonedAt = LocalDateTime.now()
    }

    fun restore() {
        if (!isTombstoned()) {
            return
        }
        tombstonedAt = null
    }
}
