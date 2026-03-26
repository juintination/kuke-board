package kuke.board.common.event.payload.like

import kuke.board.common.event.EventPayload
import java.time.LocalDateTime

data class ArticleLikedEventPayload(
    val articleLikeId: Long,
    val articleId: Long,
    val userId: Long,
    val createdAt: LocalDateTime,
    val articleLikeCount: Long,
) : EventPayload
