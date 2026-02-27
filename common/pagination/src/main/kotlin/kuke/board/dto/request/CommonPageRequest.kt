package kuke.board.dto.request

open class CommonPageRequest(
    open val page: Long = 1,
    open val size: Long = 10,
)
