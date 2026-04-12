package kuke.board.articleread.dto.response

import kuke.board.articleread.model.ArticleQueryModel
import java.time.LocalDateTime

data class ArticleReadResponse(
    val id: Long,
    val title: String,
    val content: String,
    val boardId: Long,
    val writer: WriterResponse,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
    val commentCount: Long,
    val likeCount: Long,
    val viewCount: Long,
) {
    companion object {
        fun from(
            articleQueryModel: ArticleQueryModel,
            viewCount: Long,
        ) = ArticleReadResponse(
            id = articleQueryModel.id,
            title = articleQueryModel.title,
            content = articleQueryModel.content,
            boardId = articleQueryModel.boardId,
            writer = WriterResponse(
                id = articleQueryModel.writerId,
                nickname = articleQueryModel.writerNickname,
            ),
            createdAt = articleQueryModel.createdAt,
            modifiedAt = articleQueryModel.modifiedAt,
            commentCount = articleQueryModel.commentCount,
            likeCount = articleQueryModel.likeCount,
            viewCount = viewCount,
        )

    }

    data class WriterResponse(
        val id: Long,
        val nickname: String?,
    )
}
