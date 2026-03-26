package kuke.board.articleread.client

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class CommentClient(
    @Value("\${endpoints.kuke-board-comment-service.url}")
    private val commentServiceUrl: String,
) {

    private val log = KotlinLogging.logger {}

    private val restClient: RestClient by lazy {
        RestClient.create(commentServiceUrl)
    }

    fun count(
        articleId: Long,
    ): Long {
        return try {
            restClient.get()
                .uri("/api/articles/{articleId}/comments/count", articleId)
                .retrieve()
                .body(Long::class.java)
                ?: throw IllegalStateException("Comment count response is null. articleId=$articleId")
        } catch (
            e: Exception,
        ) {
            log.error(e) { "[CommentClient.count] articleId=$articleId" }
            throw e
        }
    }
}
