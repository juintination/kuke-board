package kuke.board.dto.response

import kuke.board.dto.request.CommonPageRequest

data class CommonPageResponse<T>(
    val items: List<T>,
    val request: CommonPageRequest,
    val totalCount: Long,
    val totalPages: Long,
) {
    companion object {
        fun <T> of(
            items: List<T>,
            request: CommonPageRequest,
            totalCount: Long,
        ): CommonPageResponse<T> {
            val totalPages = if (totalCount == 0L) 0L else ((totalCount - 1) / request.size) + 1
            return CommonPageResponse(
                items = items,
                request = request,
                totalCount = totalCount,
                totalPages = totalPages,
            )
        }
    }
}
