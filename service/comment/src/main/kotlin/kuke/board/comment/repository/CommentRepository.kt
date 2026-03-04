package kuke.board.comment.repository

import kuke.board.comment.entity.Comment
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CommentRepository : JpaRepository<Comment, Long> {
    fun existsByParentId(parentId: Long): Boolean

    @Query(
        """
        select c.path.path
        from Comment c
        where c.articleId = :articleId
          and c.path.path like concat(:prefix, '%')
          and c.path.path <> :prefix
        order by c.path.path desc
        """
    )
    fun findTopPathByPrefix(
        articleId: Long,
        prefix: String,
        pageable: Pageable,
    ): List<String>
}
