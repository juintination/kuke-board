package kuke.board.like.dto.response

import kuke.board.like.entity.ArticleLike

data class ArticleLikeResponse(
    val id: Long,
    val articleId: Long,
    val userId: Long,
    val isLiked: Boolean,
) {
    companion object {
        fun from(
            articleLike: ArticleLike,
        ): ArticleLikeResponse {
            return ArticleLikeResponse(
                id = articleLike.id,
                articleId = articleLike.articleId,
                userId = articleLike.userId,
                isLiked = !articleLike.isTombstoned(),
            )
        }
    }
}
