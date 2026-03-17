package kuke.board.article.dto.request

import kuke.board.common.pagination.dto.request.CommonCursorRequest

data class ArticleCursorRequest(
    val boardId: Long,
    override val size: Int = 10,
    override val cursor: Long? = null,
) : CommonCursorRequest(
    size = size,
    cursor = cursor,
)
