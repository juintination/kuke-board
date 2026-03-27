package kuke.board.articleread.dto.response

data class ArticleReadPageResponse(
    val articles: List<ArticleReadResponse>,
) {
    companion object {
        fun of(
            articles: List<ArticleReadResponse>,
        ) = ArticleReadPageResponse(
            articles = articles,
        )
    }
}
