package kuke.board.hotarticle.client

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.time.LocalDateTime

@Component
class ArticleClient(
    @Value("\${endpoints.kuke-board-article-service.url}")
    private val articleServiceUrl: String,
) {

    private val log = KotlinLogging.logger {}

    private val restClient: RestClient by lazy {
        RestClient.create(articleServiceUrl)
    }

    fun read(
        articleId: Long,
    ): ArticleResponse? {
        return try {
            restClient.get()
                .uri("/api/articles/{articleId}", articleId)
                .retrieve()
                .body(ArticleResponse::class.java)
        } catch (
            e: Exception,
        ) {
            log.error(e) { "[ArticleClient.read] articleId=$articleId" }
            null
        }
    }

    data class ArticleResponse(
        val id: Long,
        val title: String,
        val createdAt: LocalDateTime
    )
}
