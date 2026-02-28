package kuke.board.comment.dto.request

data class CommentCreateRequest(
    val articleId: Long,
    val writerId: Long,
    val parentId: Long? = null,
    val content: String,
)
