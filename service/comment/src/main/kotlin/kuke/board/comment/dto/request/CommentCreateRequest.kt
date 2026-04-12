package kuke.board.comment.dto.request

data class CommentCreateRequest(
    val articleId: Long,
    val parentId: Long? = null,
    val content: String,
)
