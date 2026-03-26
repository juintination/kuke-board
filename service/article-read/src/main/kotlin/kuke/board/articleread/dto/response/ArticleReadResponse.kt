package kuke.board.articleread.dto.response

import kuke.board.articleread.model.ArticleQueryModel
import java.time.LocalDateTime

data class ArticleReadResponse(
    val id: Long,
    val title: String,
    val content: String,
    val boardId: Long,
    val writerId: Long,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
    val commentCount: Long,
    val likeCount: Long,
    val viewCount: Long,
) {
    companion object {
        fun from(
            articleQueryModel: ArticleQueryModel,
        ) = ArticleReadResponse(
            id = articleQueryModel.id,
            title = articleQueryModel.title,
            content = articleQueryModel.content,
            boardId = articleQueryModel.boardId,
            writerId = articleQueryModel.writerId,
            createdAt = articleQueryModel.createdAt,
            modifiedAt = articleQueryModel.modifiedAt,
            commentCount = articleQueryModel.commentCount,
            likeCount = articleQueryModel.likeCount,
            viewCount = articleQueryModel.viewCount,
        )
    }
}
