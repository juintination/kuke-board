package kuke.board.comment.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "article_comment_count")
class ArticleCommentCount(

    @Id
    @Column(name = "article_id", columnDefinition = "BIGINT UNSIGNED")
    val articleId: Long,

    @Column(name = "comment_count", nullable = false)
    var commentCount: Long = 0,
) {
    companion object {
        fun create(
            articleId: Long,
        ) = ArticleCommentCount(
            articleId = articleId,
        )
    }
}
