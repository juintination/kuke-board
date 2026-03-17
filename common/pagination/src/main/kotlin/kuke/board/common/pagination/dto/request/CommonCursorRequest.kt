package kuke.board.common.pagination.dto.request

open class CommonCursorRequest(
    open val size: Int = 10,
    open val cursor: Long? = null,
)
