package kuke.board.article.controller

import kuke.board.article.dto.request.ArticleCreateRequest
import kuke.board.article.dto.request.ArticleCursorRequest
import kuke.board.article.dto.request.ArticlePageRequest
import kuke.board.article.dto.request.ArticleUpdateRequest
import kuke.board.article.dto.response.ArticleResponse
import kuke.board.article.service.ArticleService
import kuke.board.dto.response.CommonCursorResponse
import kuke.board.dto.response.CommonPageResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/articles")
class ArticleController(
    private val articleService: ArticleService
) {

    @PostMapping
    fun create(
        @RequestBody request: ArticleCreateRequest
    ): ArticleResponse {
        return articleService.create(request)
    }

    @GetMapping("/{articleId}")
    fun read(
        @PathVariable articleId: Long
    ): ArticleResponse {
        return articleService.read(articleId)
    }

    @GetMapping
    fun readAll(
        request: ArticlePageRequest,
    ): CommonPageResponse<ArticleResponse> {
        return articleService.readAll(
            request = request,
        )
    }

    @GetMapping("/cursor")
    fun readAllCursor(
        request: ArticleCursorRequest,
    ): CommonCursorResponse<ArticleResponse> {
        return articleService.readAllCursor(request)
    }

    @PutMapping("/{articleId}")
    fun update(
        @PathVariable articleId: Long,
        @RequestBody request: ArticleUpdateRequest
    ): ArticleResponse {
        return articleService.update(articleId, request)
    }

    @DeleteMapping("/{articleId}")
    fun delete(
        @PathVariable articleId: Long
    ) {
        articleService.delete(articleId)
    }
}
