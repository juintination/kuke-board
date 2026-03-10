package kuke.board.view.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "article_view_count")
class ArticleViewCount(

    @Id
    @Column(name = "article_id", columnDefinition = "BIGINT UNSIGNED")
    val articleId: Long,

    @Column(name = "view_count", nullable = false)
    var viewCount: Long = 0,
) {
    companion object {
        fun create(
            articleId: Long,
            viewCount: Long,
        ) = ArticleViewCount(
            articleId = articleId,
            viewCount = viewCount,
        )
    }
}
