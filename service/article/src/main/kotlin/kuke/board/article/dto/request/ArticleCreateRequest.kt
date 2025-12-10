package kuke.board.article.dto.request

data class ArticleCreateRequest(
    val title: String,
    val content: String,
    val boardId: Long,
    val writerId: Long
)
