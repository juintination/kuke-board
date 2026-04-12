package kuke.board.articleread.service

import io.github.oshai.kotlinlogging.KotlinLogging
import kuke.board.articleread.client.*
import kuke.board.articleread.dto.response.ArticleReadPageResponse
import kuke.board.articleread.dto.response.ArticleReadResponse
import kuke.board.articleread.model.ArticleQueryModel
import kuke.board.articleread.repository.ArticleIdListRepository
import kuke.board.articleread.repository.ArticleQueryModelRepository
import kuke.board.articleread.service.event.EventHandler
import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class ArticleReadService(
    private val articleQueryModelRepository: ArticleQueryModelRepository,
    private val articleIdListRepository: ArticleIdListRepository,
    private val articleClient: ArticleClient,
    private val commentClient: CommentClient,
    private val likeClient: LikeClient,
    private val viewClient: ViewClient,
    private val userClient: UserClient,
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
        )?.let { model ->
            hydrate(
                model = model,
            )
        } ?: fetch(
            articleId = articleId,
        )

        return ArticleReadResponse.from(
            articleQueryModel = articleQueryModel!!,
            viewCount = viewClient.count(
                articleId = articleId,
            ),
        )
    }

    fun readAll(
        boardId: Long,
        page: Long,
        size: Long,
    ): ArticleReadPageResponse {
        val articleIds = readAllArticleIds(
            boardId = boardId,
            page = page,
            size = size,
        )

        val articles = readAll(
            articleIds = articleIds,
        )

        return ArticleReadPageResponse.of(
            articles = articles,
        )
    }

    private fun readAll(
        articleIds: List<Long>,
    ): List<ArticleReadResponse> {
        val articleMap = articleQueryModelRepository.readAll(
            articleIds = articleIds,
        )

        return articleIds.mapNotNull { articleId ->
            val articleQueryModel = articleMap[articleId]?.let { model ->
                hydrate(
                    model = model,
                )
            } ?: fetch(
                articleId = articleId,
            )

            articleQueryModel?.let {
                ArticleReadResponse.from(
                    articleQueryModel = it,
                    viewCount = viewClient.count(it.id),
                )
            }
        }
    }

    private fun readAllArticleIds(
        boardId: Long,
        page: Long,
        size: Long,
    ): List<Long> {
        val articleIds = articleIdListRepository.readAll(boardId, (page - 1) * size, size)
        return if (articleIds.size == size.toInt()) {
            log.info { "[ArticleReadService.readAllArticleIds] return redis data." }
            articleIds
        } else {
            log.info { "[ArticleReadService.readAllArticleIds] return origin data." }
            articleClient.readAll(boardId, page, size)!!.items.map { it.id }
        }
    }

    fun readAllCursor(
        boardId: Long,
        cursor: Long?,
        size: Int,
    ): ArticleReadPageResponse {
        val articleIds = readAllCursorArticleIds(
            boardId = boardId,
            cursor = cursor,
            size = size,
        )

        val articles = readAll(
            articleIds = articleIds,
        )

        return ArticleReadPageResponse.of(
            articles = articles,
        )
    }

    private fun readAllCursorArticleIds(
        boardId: Long,
        cursor: Long?,
        size: Int,
    ): List<Long> {
        val articleIds = articleIdListRepository.readAllInfiniteScroll(
            boardId = boardId,
            lastArticleId = cursor,
            limit = size,
        )

        return if (articleIds.size == size) {
            log.info { "[ArticleReadService.readAllCursorArticleIds] return redis data." }
            articleIds
        } else {
            log.info { "[ArticleReadService.readAllCursorArticleIds] return origin data." }
            articleClient.readAllCursor(
                boardId = boardId,
                size = size,
                cursor = cursor,
            )!!.items.map { it.id }
        }
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
        }?.let { model ->
            hydrate(
                model = model,
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

    private fun hydrate(
        model: ArticleQueryModel,
    ): ArticleQueryModel {
        if (model.writerNickname == null) {
            model.writerNickname = userClient.read(model.writerId)?.nickname
        }
        return model
    }
}
