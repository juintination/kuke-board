package kuke.board.comment.dto.request

import kuke.board.common.pagination.dto.request.CommonCursorRequest

data class CommentCursorRequest(
    val articleId: Long,
    val parentId: Long? = null,
    override val size: Int = 10,
    override val cursor: Long? = null,
) : CommonCursorRequest(
    size = size,
    cursor = cursor,
)
