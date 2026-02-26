package kuke.board.article.dto.request

data class ArticlePageRequest(
    val boardId: Long,
    val page: Long = 1,
    val size: Long = 10,
)
