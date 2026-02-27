package kuke.board.article.service

import kuke.board.article.dto.request.ArticleCreateRequest
import kuke.board.article.dto.request.ArticleCursorRequest
import kuke.board.article.dto.request.ArticlePageRequest
import kuke.board.article.dto.request.ArticleUpdateRequest
import kuke.board.article.dto.response.ArticleResponse
import kuke.board.article.entity.Article
import kuke.board.article.repository.ArticleRepository
import kuke.board.common.snowflake.Snowflake
import kuke.board.dto.response.CommonCursorResponse
import kuke.board.dto.response.CommonPageResponse
import kuke.board.util.PageLimitCalculator
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleService(
    private val articleRepository: ArticleRepository
) {

    private val snowflake = Snowflake()

    @Transactional
    fun create(
        request: ArticleCreateRequest
    ): ArticleResponse {
        val article = Article.create(
            id = snowflake.nextId(),
            title = request.title,
            content = request.content,
            boardId = request.boardId,
            writerId = request.writerId,
        )

        return ArticleResponse.from(articleRepository.save(article))
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
        articleId: Long,
        request: ArticleUpdateRequest
    ): ArticleResponse {
        val article = getArticleOrThrow(articleId)
        article.update(request)
        return ArticleResponse.from(article)
    }

    @Transactional
    fun delete(
        articleId: Long
    ) {
        val article = getArticleOrThrow(articleId)
        articleRepository.delete(article)
    }

    private fun getArticleOrThrow(
        articleId: Long
    ): Article = articleRepository.findByIdOrNull(articleId)
        ?: throw IllegalArgumentException("존재하지 않는 게시글입니다. articleId: $articleId")
}
