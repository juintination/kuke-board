package kuke.board.like.repository

import kuke.board.like.entity.ArticleLikeCount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface ArticleLikeCountRepository : JpaRepository<ArticleLikeCount, Long> {

    @Modifying
    @Query(
        """
        update ArticleLikeCount c
        set c.likeCount = c.likeCount + 1
        where c.articleId = :articleId
        """
    )
    fun increase(articleId: Long): Int

    @Modifying
    @Query(
        """
        update ArticleLikeCount c
        set c.likeCount = c.likeCount - 1
        where c.articleId = :articleId
        """
    )
    fun decrease(articleId: Long): Int
}
