package kuke.board.articleread.client

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class ViewClient(
    @Value("\${endpoints.kuke-board-view-service.url}")
    private val viewServiceUrl: String,
) {

    private val log = KotlinLogging.logger {}

    private val restClient: RestClient by lazy {
        RestClient.create(viewServiceUrl)
    }

    fun count(
        articleId: Long,
    ): Long {
        return try {
            restClient.get()
                .uri("/api/articles/{articleId}/views/count", articleId)
                .retrieve()
                .body(Long::class.java)
                ?: throw IllegalStateException("View count response is null. articleId=$articleId")
        } catch (
            e: Exception,
        ) {
            log.error(e) { "[ViewClient.count] articleId=$articleId" }
            throw e
        }
    }
}
