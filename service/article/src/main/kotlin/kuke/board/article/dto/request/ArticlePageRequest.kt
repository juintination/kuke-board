package kuke.board.article.dto.request

import kuke.board.dto.request.CommonPageRequest

data class ArticlePageRequest(
    val boardId: Long,
    override val page: Long = 1,
    override val size: Long = 10,
) : CommonPageRequest(
    page = page,
    size = size,
)
