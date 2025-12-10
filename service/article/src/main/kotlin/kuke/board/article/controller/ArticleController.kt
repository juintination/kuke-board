package kuke.board.article.controller

import kuke.board.article.dto.request.ArticleCreateRequest
import kuke.board.article.dto.request.ArticleUpdateRequest
import kuke.board.article.dto.response.ArticleResponse
import kuke.board.article.service.ArticleService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/articles")
class ArticleController(
    private val articleService: ArticleService
) {

    @PostMapping
    fun create(
        @RequestBody req: ArticleCreateRequest
    ): ArticleResponse {
        return articleService.create(req)
    }

    @GetMapping("/{articleId}")
    fun read(
        @PathVariable articleId: Long
    ): ArticleResponse {
        return articleService.read(articleId)
    }

    @PutMapping("/{articleId}")
    fun update(
        @PathVariable articleId: Long,
        @RequestBody req: ArticleUpdateRequest
    ): ArticleResponse {
        return articleService.update(articleId, req)
    }

    @DeleteMapping("/{articleId}")
    fun delete(
        @PathVariable articleId: Long
    ) {
        articleService.delete(articleId)
    }
}
