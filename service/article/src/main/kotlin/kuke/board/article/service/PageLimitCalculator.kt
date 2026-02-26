package kuke.board.article.service

object PageLimitCalculator {
    fun calculatePageLimit(
        page: Long,
        pageSize: Long,
        movablePageCount: Long
    ) = (((page - 1) / movablePageCount) + 1) * pageSize * movablePageCount + 1
}
