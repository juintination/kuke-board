package kuke.board.articleread.controller

import kuke.board.articleread.dto.response.ArticleReadPageResponse
import kuke.board.articleread.dto.response.ArticleReadResponse
import kuke.board.articleread.service.ArticleReadService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/articles")
class ArticleReadController(
    private val articleReadService: ArticleReadService,
) {

    @GetMapping("{articleId}")
    fun read(
        @PathVariable articleId: Long,
    ): ArticleReadResponse {
        return articleReadService.read(articleId)
    }

    @GetMapping
    fun readAll(
        @RequestParam boardId: Long,
        @RequestParam page: Long,
        @RequestParam size: Long,
    ): ArticleReadPageResponse {
        return articleReadService.readAll(boardId, page, size)
    }

    @GetMapping("/cursor")
    fun readAllCursor(
        @RequestParam boardId: Long,
        @RequestParam cursor: Long?,
        @RequestParam size: Int,
    ): ArticleReadPageResponse {
        return articleReadService.readAllCursor(boardId, cursor, size)
    }
}
