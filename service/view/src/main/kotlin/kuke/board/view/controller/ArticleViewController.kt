package kuke.board.view.controller

import kuke.board.view.service.ArticleViewService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/articles/{articleId}/views")
class ArticleViewController(
    private val articleViewService: ArticleViewService
) {

    @PostMapping
    fun increase(
        @PathVariable articleId: Long,
    ): Long {
        return articleViewService.increase(
            articleId = articleId,
        )
    }

    @GetMapping("/count")
    fun count(
        @PathVariable articleId: Long,
    ): Long {
        return articleViewService.count(
            articleId = articleId,
        )
    }
}
