package kuke.board.article.service

import kuke.board.article.dto.request.ArticleCreateRequest
import kuke.board.article.dto.request.ArticleUpdateRequest
import kuke.board.article.dto.response.ArticleResponse
import kuke.board.article.entity.Article
import kuke.board.article.repository.ArticleRepository
import kuke.board.common.snowflake.Snowflake
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
