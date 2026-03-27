package kuke.board.articleread.client

import io.github.oshai.kotlinlogging.KotlinLogging
import kuke.board.common.pagination.dto.response.CommonCursorResponse
import kuke.board.common.pagination.dto.response.CommonPageResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
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

    fun readAll(
        boardId: Long,
        page: Long,
        pageSize: Long,
    ): CommonPageResponse<ArticleResponse>? {
        return try {
            val typeRef = object : ParameterizedTypeReference<CommonPageResponse<ArticleResponse>>() {}
            restClient.get()
                .uri { uriBuilder ->
                    uriBuilder.path("/api/articles")
                        .queryParam("boardId", boardId)
                        .queryParam("page", page)
                        .queryParam("size", pageSize)
                        .build()
                }
                .retrieve()
                .body(typeRef)
        } catch (
            e: Exception,
        ) {
            log.error(e) { "[ArticleClient.readAll] boardId=$boardId, page=$page, pageSize=$pageSize" }
            null
        }
    }

    fun readAllCursor(
        boardId: Long,
        size: Int,
        cursor: Long?,
    ): CommonCursorResponse<ArticleResponse>? {
        return try {
            val typeRef = object : ParameterizedTypeReference<CommonCursorResponse<ArticleResponse>>() {}
            restClient.get()
                .uri { uriBuilder ->
                    val builder = uriBuilder.path("/api/articles/cursor")
                        .queryParam("boardId", boardId)
                        .queryParam("size", size)
                    cursor?.let { builder.queryParam("cursor", it) }
                    builder.build()
                }
                .retrieve()
                .body(typeRef)
        } catch (
            e: Exception,
        ) {
            log.error(e) { "[ArticleClient.readAllCursor] boardId=$boardId, size=$size, cursor=$cursor" }
            null
        }
    }

    data class ArticleResponse(
        val id: Long,
        val title: String,
        val content: String,
        val boardId: Long,
        val writerId: Long,
        val createdAt: LocalDateTime,
        val modifiedAt: LocalDateTime,
    )
}
