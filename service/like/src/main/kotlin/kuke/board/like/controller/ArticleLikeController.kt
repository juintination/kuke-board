package kuke.board.like.controller

import kuke.board.like.dto.response.ArticleLikeResponse
import kuke.board.like.service.ArticleLikeService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/articles/{articleId}/likes")
class ArticleLikeController(
    private val articleLikeService: ArticleLikeService,
) {

    @PostMapping("/users/{userId}/toggle")
    fun toggle(
        @PathVariable articleId: Long,
        @PathVariable userId: Long,
    ): ArticleLikeResponse {
        return articleLikeService.toggle(
            articleId = articleId,
            userId = userId,
        )
    }

    @GetMapping("/users/{userId}/is-liked")
    fun isLiked(
        @PathVariable articleId: Long,
        @PathVariable userId: Long,
    ): Boolean {
        return articleLikeService.isLiked(
            articleId = articleId,
            userId = userId,
        )
    }
}
