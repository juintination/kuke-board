package kuke.board.article.service

import kuke.board.article.dto.request.ArticleCreateRequest
import kuke.board.article.dto.request.ArticleCursorRequest
import kuke.board.article.dto.request.ArticlePageRequest
import kuke.board.article.dto.request.ArticleUpdateRequest
import kuke.board.article.dto.response.ArticleResponse
import kuke.board.article.entity.Article
import kuke.board.article.repository.ArticleRepository
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.article.ArticleCreatedEventPayload
import kuke.board.common.event.payload.article.ArticleDeletedEventPayload
import kuke.board.common.event.payload.article.ArticleUpdatedEventPayload
import kuke.board.common.outbox.event.OutboxEventPublisher
import kuke.board.common.pagination.dto.response.CommonCursorResponse
import kuke.board.common.pagination.dto.response.CommonPageResponse
import kuke.board.common.pagination.util.PageLimitCalculator
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val outboxEventPublisher: OutboxEventPublisher,
) {

    @Transactional
    fun create(
        userId: Long,
        request: ArticleCreateRequest
    ): ArticleResponse {
        val article = articleRepository.save(
            Article.create(
                writerId = userId,
                request = request,
            )
        )
        outboxEventPublisher.publish(
            eventType = EventType.ARTICLE_CREATED,
            payload = ArticleCreatedEventPayload(
                articleId = article.id!!,
                title = article.title,
                content = article.content,
                boardId = article.boardId,
                writerId = article.writerId,
                createdAt = article.createdAt,
                modifiedAt = article.modifiedAt,
            ),
            shardKey = article.boardId,
        )

        return ArticleResponse.from(article)
    }

    @Transactional(readOnly = true)
    fun read(
        articleId: Long
    ): ArticleResponse {
        val article = getArticleOrThrow(articleId)
        return ArticleResponse.from(article)
    }

    @Transactional(readOnly = true)
    fun readAll(
        request: ArticlePageRequest,
    ): CommonPageResponse<ArticleResponse> {
        val pageable = PageRequest.of(
            (request.page - 1).toInt(),
            request.size.toInt(),
        )

        val articles = articleRepository.findAll(request.boardId, pageable)
            .map(ArticleResponse::from)

        val countLimit = PageLimitCalculator.calculatePageLimit(
            page = request.page,
            pageSize = request.size,
            movablePageCount = 10L
        )

        val totalCount = articleRepository.countAll(request.boardId)
            .coerceAtMost(countLimit)

        return CommonPageResponse.of(
            items = articles,
            request = request,
            totalCount = totalCount
        )
    }

    @Transactional(readOnly = true)
    fun readAllCursor(
        request: ArticleCursorRequest,
    ): CommonCursorResponse<ArticleResponse> {

        val pageable = PageRequest.of(
            0,
            request.size + 1
        )

        val fetched = articleRepository.findAllByCursor(
            boardId = request.boardId,
            lastId = request.cursor,
            pageable = pageable,
        )

        val hasNext = fetched.size > request.size
        val pageItems = fetched.take(request.size)

        val items = pageItems.map(ArticleResponse::from)
        val nextCursor = if (hasNext) pageItems.last().id else null

        return CommonCursorResponse.of(
            items = items,
            nextCursorId = nextCursor,
            hasNext = hasNext,
        )
    }

    @Transactional
    fun update(
        userId: Long,
        articleId: Long,
        request: ArticleUpdateRequest
    ): ArticleResponse {
        val article = getArticleOrThrow(articleId)

        validateWriter(
            userId = userId,
            article = article,
        )

        article.update(request)

        outboxEventPublisher.publish(
            eventType = EventType.ARTICLE_UPDATED,
            payload = ArticleUpdatedEventPayload(
                articleId = article.id!!,
                title = article.title,
                content = article.content,
                boardId = article.boardId,
                writerId = article.writerId,
                createdAt = article.createdAt,
                modifiedAt = article.modifiedAt,
            ),
            shardKey = article.boardId,
        )

        return ArticleResponse.from(article)
    }

    @Transactional
    fun delete(
        userId: Long,
        articleId: Long
    ) {
        val article = getArticleOrThrow(articleId)

        validateWriter(
            userId = userId,
            article = article,
        )

        articleRepository.delete(article)

        outboxEventPublisher.publish(
            eventType = EventType.ARTICLE_DELETED,
            payload = ArticleDeletedEventPayload(
                articleId = article.id!!,
                title = article.title,
                content = article.content,
                boardId = article.boardId,
                writerId = article.writerId,
                createdAt = article.createdAt,
                modifiedAt = article.modifiedAt,
            ),
            shardKey = article.boardId,
        )
    }

    private fun getArticleOrThrow(
        articleId: Long
    ): Article = articleRepository.findByIdOrNull(articleId)
        ?: throw IllegalArgumentException("존재하지 않는 게시글입니다. articleId: $articleId")

    private fun validateWriter(
        userId: Long,
        article: Article,
    ) {
        if (article.writerId != userId) {
            throw IllegalArgumentException("작성자만 수정/삭제할 수 있습니다. articleId: ${article.id}, userId: $userId")
        }
    }
}
