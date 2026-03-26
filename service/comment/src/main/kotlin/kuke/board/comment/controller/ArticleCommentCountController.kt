package kuke.board.comment.controller

import kuke.board.comment.service.ArticleCommentCountService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/articles/{articleId}/comments/count")
class ArticleCommentCountController(
    private val articleCommentCountService: ArticleCommentCountService,
) {

    @GetMapping
    fun count(
        @PathVariable articleId: Long
    ): Long {
        return articleCommentCountService.getCommentCount(articleId)
    }
}
