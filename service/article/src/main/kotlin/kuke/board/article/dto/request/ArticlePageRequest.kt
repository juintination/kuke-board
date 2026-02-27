package kuke.board.article.dto.request

data class ArticlePageRequest(
    val page: Long = 1,
    val size: Long = 10,
)
