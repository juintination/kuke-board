package kuke.board.articleread.service

import io.github.oshai.kotlinlogging.KotlinLogging
import kuke.board.articleread.client.ArticleClient
import kuke.board.articleread.client.CommentClient
import kuke.board.articleread.client.LikeClient
import kuke.board.articleread.client.ViewClient
import kuke.board.articleread.dto.response.ArticleReadResponse
import kuke.board.articleread.model.ArticleQueryModel
import kuke.board.articleread.repository.ArticleQueryModelRepository
import kuke.board.articleread.service.event.EventHandler
import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class ArticleReadService(
    private val articleQueryModelRepository: ArticleQueryModelRepository,
    private val articleClient: ArticleClient,
    private val commentClient: CommentClient,
    private val likeClient: LikeClient,
    private val viewClient: ViewClient,
    private val eventHandlers: List<EventHandler<*>>,
) {

    private val log = KotlinLogging.logger {}

    fun handleEvent(
        event: Event<EventPayload>,
    ) {
        val handler = findHandler(
            event = event,
        )

        if (handler == null) {
            log.warn { "[ArticleReadService.handleEvent] No handler found for eventType=${event.type}" }
            return
        }

        handler.handle(
            event = event,
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun findHandler(
        event: Event<EventPayload>,
    ): EventHandler<EventPayload>? =
        eventHandlers
            .asSequence()
            .map { it as EventHandler<EventPayload> }
            .firstOrNull { it.supports(event) }

    fun read(
        articleId: Long,
    ): ArticleReadResponse {
        val articleQueryModel = articleQueryModelRepository.read(
            articleId = articleId,
        ) ?: fetch(
            articleId = articleId,
        )

        return ArticleReadResponse.from(
            articleQueryModel = articleQueryModel!!,
            viewCount = viewClient.count(
                articleId = articleId,
            ),
        )
    }

    private fun fetch(
        articleId: Long,
    ): ArticleQueryModel? {
        val articleQueryModel = articleClient.read(
            articleId = articleId,
        )?.let { article ->
            ArticleQueryModel.create(
                article = article,
                commentCount = commentClient.count(articleId),
                likeCount = likeClient.count(articleId),
            )
        }

        articleQueryModel?.let {
            articleQueryModelRepository.create(
                articleQueryModel = it,
                ttl = Duration.ofDays(1),
            )
        }

        log.info { "[ArticleReadService.fetch] fetch data. articleId=$articleId isPresent=${articleQueryModel != null}" }

        return articleQueryModel
    }
}
