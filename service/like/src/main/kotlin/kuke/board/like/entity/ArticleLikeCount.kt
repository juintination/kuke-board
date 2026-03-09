package kuke.board.like.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "article_like_count")
class ArticleLikeCount(

    @Id
    @Column(name = "article_id", columnDefinition = "BIGINT UNSIGNED")
    val articleId: Long,

    @Column(name = "like_count", nullable = false)
    var likeCount: Long = 0,
) {
    companion object {
        fun create(
            articleId: Long,
        ) = ArticleLikeCount(
            articleId = articleId,
        )
    }
}
