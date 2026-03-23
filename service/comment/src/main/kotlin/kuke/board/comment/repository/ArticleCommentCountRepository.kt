package kuke.board.comment.repository

import kuke.board.comment.entity.ArticleCommentCount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface ArticleCommentCountRepository : JpaRepository<ArticleCommentCount, Long> {

    @Modifying
    @Query(
        """
        update ArticleCommentCount c
        set c.commentCount = c.commentCount + 1
        where c.articleId = :articleId
        """
    )
    fun increase(articleId: Long): Int

    @Modifying
    @Query(
        """
        update ArticleCommentCount c
        set c.commentCount = c.commentCount - 1
        where c.articleId = :articleId
        """
    )
    fun decrease(articleId: Long): Int
}
