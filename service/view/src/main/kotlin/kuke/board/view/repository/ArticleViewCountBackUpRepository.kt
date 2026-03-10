package kuke.board.view.repository

import kuke.board.view.entity.ArticleViewCount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ArticleViewCountBackUpRepository : JpaRepository<ArticleViewCount, Long> {

    @Modifying
    @Query(
        """
        update ArticleViewCount a
        set a.viewCount = :viewCount
        where a.articleId = :articleId
        and a.viewCount < :viewCount
        """
    )
    fun updateViewCount(
        articleId: Long,
        viewCount: Long,
    ): Int
}
