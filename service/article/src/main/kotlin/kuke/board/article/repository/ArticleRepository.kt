package kuke.board.article.repository

import kuke.board.article.entity.Article
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ArticleRepository : JpaRepository<Article, Long> {

    @Query(
        """
        select a
        from Article a
        where a.boardId = :boardId
        order by a.id desc
        """
    )
    fun findAll(boardId: Long, pageable: Pageable): List<Article>

    @Query(
        """
        select count(a)
        from Article a
        where a.boardId = :boardId
        """
    )
    fun countAll(boardId: Long): Long

    @Query(
        """
        select a
        from Article a
        where a.boardId = :boardId
          and (:lastId is null or a.id < :lastId)
        order by a.id desc
        """
    )
    fun findAllByCursor(
        boardId: Long,
        lastId: Long?,
        pageable: Pageable,
    ): List<Article>
}
