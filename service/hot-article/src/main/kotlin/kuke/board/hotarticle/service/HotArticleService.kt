package kuke.board.hotarticle.service

import io.github.oshai.kotlinlogging.KotlinLogging
import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload
import kuke.board.common.event.EventType
import kuke.board.hotarticle.client.ArticleClient
import kuke.board.hotarticle.dto.response.HotArticleResponse
import kuke.board.hotarticle.repository.HotArticleListRepository
import kuke.board.hotarticle.service.event.EventHandler
import org.springframework.stereotype.Service

@Service
class HotArticleService(
    private val articleClient: ArticleClient,
    private val eventHandlers: List<EventHandler<*>>,
    private val hotArticleScoreUpdater: HotArticleScoreUpdater,
    private val hotArticleListRepository: HotArticleListRepository,
) {

    private val log = KotlinLogging.logger {}

    fun handleEvent(
        event: Event<EventPayload>,
    ) {
        log.info { "[HotArticleService.handleEvent] handling event=$event" }
        val handler = findHandler(
            event = event,
        )

        if (handler == null) {
            log.warn { "[HotArticleService.handleEvent] No handler found for eventType=${event.type}" }
            return
        }

        when (event.type) {
            EventType.ARTICLE_CREATED,
            EventType.ARTICLE_DELETED ->
                handler.handle(
                    event = event,
                )

            else ->
                hotArticleScoreUpdater.update(
                    event = event,
                    eventHandler = handler,
                )
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun findHandler(
        event: Event<EventPayload>,
    ): EventHandler<EventPayload>? {
        return eventHandlers
            .asSequence()
            .map { it as EventHandler<EventPayload> }
            .firstOrNull { it.supports(event) }
    }

    fun readAll(
        dateStr: String,
    ): List<HotArticleResponse> =
        hotArticleListRepository.readAll(dateStr)
            .asSequence()
            .mapNotNull(articleClient::read)
            .map(HotArticleResponse::from)
            .toList()
}
