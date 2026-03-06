package kuke.board.like.repository

import kuke.board.like.entity.ArticleLike
import org.springframework.data.jpa.repository.JpaRepository

interface ArticleLikeRepository : JpaRepository<ArticleLike, Long> {

    fun findByArticleIdAndUserId(articleId: Long, userId: Long): ArticleLike?

    fun existsByArticleIdAndUserIdAndTombstonedAtIsNull(
        articleId: Long,
        userId: Long,
    ): Boolean
}
