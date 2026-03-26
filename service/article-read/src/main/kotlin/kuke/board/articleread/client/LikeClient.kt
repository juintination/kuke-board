package kuke.board.articleread.client

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class LikeClient(
    @Value("\${endpoints.kuke-board-like-service.url}")
    private val likeServiceUrl: String,
) {

    private val log = KotlinLogging.logger {}

    private val restClient: RestClient by lazy {
        RestClient.create(likeServiceUrl)
    }

    fun count(
        articleId: Long,
    ): Long {
        return try {
            restClient.get()
                .uri("/api/articles/{articleId}/likes/count", articleId)
                .retrieve()
                .body(Long::class.java)
                ?: throw IllegalStateException("Like count response is null. articleId=$articleId")
        } catch (
            e: Exception,
        ) {
            log.error(e) { "[LikeClient.count] articleId=$articleId" }
            throw e
        }
    }
}
