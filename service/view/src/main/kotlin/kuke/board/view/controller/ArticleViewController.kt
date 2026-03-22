package kuke.board.view.controller

import kuke.board.view.service.ArticleViewService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/articles/{articleId}/views")
class ArticleViewController(
    private val articleViewService: ArticleViewService
) {

    @PostMapping("/users/{userId}")
    fun increase(
        @PathVariable articleId: Long,
        @PathVariable userId: Long,
    ): Long {
        return articleViewService.increase(
            articleId = articleId,
            userId = userId,
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
