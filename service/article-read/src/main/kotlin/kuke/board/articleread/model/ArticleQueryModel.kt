package kuke.board.articleread.model

import kuke.board.articleread.client.ArticleClient
import kuke.board.common.event.EventPayload
import kuke.board.common.event.payload.article.ArticleCreatedEventPayload
import kuke.board.common.event.payload.article.ArticleUpdatedEventPayload
import kuke.board.common.event.payload.comment.CommentCreatedEventPayload
import kuke.board.common.event.payload.comment.CommentDeletedEventPayload
import kuke.board.common.event.payload.like.ArticleLikedEventPayload
import kuke.board.common.event.payload.like.ArticleUnlikedEventPayload
import kuke.board.common.event.payload.view.ArticleViewedEventPayload
import java.time.LocalDateTime

data class ArticleQueryModel(
    val id: Long,
    var title: String,
    var content: String,
    var boardId: Long,
    val writerId: Long,
    val createdAt: LocalDateTime,
    var modifiedAt: LocalDateTime,
    var commentCount: Long,
    var likeCount: Long,
    var viewCount: Long,
) {
    companion object {
        fun create(
            payload: ArticleCreatedEventPayload,
        ) = ArticleQueryModel(
            id = payload.articleId,
            title = payload.title,
            content = payload.content,
            boardId = payload.boardId,
            writerId = payload.writerId,
            createdAt = payload.createdAt,
            modifiedAt = payload.modifiedAt,
            commentCount = 0L,
            likeCount = 0L,
            viewCount = 0L,
        )

        fun create(
            article: ArticleClient.ArticleResponse,
            commentCount: Long,
            likeCount: Long,
            viewCount: Long,
        ) = ArticleQueryModel(
            id = article.id,
            title = article.title,
            content = article.content,
            boardId = article.boardId,
            writerId = article.writerId,
            createdAt = article.createdAt,
            modifiedAt = article.modifiedAt,
            commentCount = commentCount,
            likeCount = likeCount,
            viewCount = viewCount,
        )
    }

    fun apply(
        payload: EventPayload,
    ) {
        when (payload) {
            is CommentCreatedEventPayload ->
                commentCount = payload.articleCommentCount

            is CommentDeletedEventPayload ->
                commentCount = payload.articleCommentCount

            is ArticleLikedEventPayload ->
                likeCount = payload.articleLikeCount

            is ArticleUnlikedEventPayload ->
                likeCount = payload.articleLikeCount

            is ArticleViewedEventPayload ->
                viewCount = payload.articleViewCount

            is ArticleUpdatedEventPayload -> {
                title = payload.title
                content = payload.content
                boardId = payload.boardId
                modifiedAt = payload.modifiedAt
            }
        }
    }
}
