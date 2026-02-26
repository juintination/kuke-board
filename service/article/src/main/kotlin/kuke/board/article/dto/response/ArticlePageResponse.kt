package kuke.board.article.dto.response

data class ArticlePageResponse(
    val items: List<ArticleResponse>,
    val page: Long,
    val size: Long,
    val totalCount: Long,
    val totalPages: Long,
) {
    companion object {
        fun of(
            items: List<ArticleResponse>,
            page: Long,
            size: Long,
            totalCount: Long,
        ): ArticlePageResponse {
            val totalPages = if (totalCount == 0L) 0L else ((totalCount - 1) / size) + 1
            return ArticlePageResponse(
                items = items,
                page = page,
                size = size,
                totalCount = totalCount,
                totalPages = totalPages,
            )
        }
    }
}
