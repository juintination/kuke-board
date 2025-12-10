package kuke.board.article.dto.response

import kuke.board.article.entity.Article
import java.time.LocalDateTime

data class ArticleResponse(
    val articleId: Long,
    val title: String,
    val content: String,
    val boardId: Long,
    val writerId: Long,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime
) {
    companion object {
        fun from(entity: Article) = ArticleResponse(
            articleId = requireNotNull(entity.articleId),
            title = entity.title,
            content = entity.content,
            boardId = requireNotNull(entity.boardId),
            writerId = requireNotNull(entity.writerId),
            createdAt = requireNotNull(entity.createdAt),
            modifiedAt = requireNotNull(entity.modifiedAt)
        )
    }
}
