package kuke.board.dto.response

data class CommonCursorResponse<T>(
    val items: List<T>,
    val nextCursorId: Long?,
    val hasNext: Boolean,
) {
    companion object {
        fun <T> of(
            items: List<T>,
            nextCursorId: Long?,
            hasNext: Boolean,
        ): CommonCursorResponse<T> = CommonCursorResponse(
            items = items,
            nextCursorId = nextCursorId,
            hasNext = hasNext,
        )
    }
}
