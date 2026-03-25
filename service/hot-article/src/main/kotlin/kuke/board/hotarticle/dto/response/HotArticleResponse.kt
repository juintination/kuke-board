package kuke.board.hotarticle.dto.response

import kuke.board.hotarticle.client.ArticleClient
import java.time.LocalDateTime

data class HotArticleResponse(
    val id: Long,
    val title: String,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(
            articleResponse: ArticleClient.ArticleResponse,
        ) = HotArticleResponse(
            id = articleResponse.id,
            title = articleResponse.title,
            createdAt = articleResponse.createdAt,
        )
    }
}
