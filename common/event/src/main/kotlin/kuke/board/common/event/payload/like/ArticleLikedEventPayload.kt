package kuke.board.common.event.payload.like

import kuke.board.common.event.EventPayload
import java.time.LocalDateTime

data class ArticleLikedEventPayload(
    val articleLikeId: Long? = null,
    val articleId: Long? = null,
    val userId: Long? = null,
    val createdAt: LocalDateTime? = null,
    val articleLikeCount: Long? = null,
) : EventPayload
