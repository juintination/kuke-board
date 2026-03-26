package kuke.board.articleread.controller

import kuke.board.articleread.dto.response.ArticleReadResponse
import kuke.board.articleread.service.ArticleReadService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
}
