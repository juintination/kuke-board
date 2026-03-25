package kuke.board.common.event

import io.github.oshai.kotlinlogging.KotlinLogging
import kuke.board.common.event.payload.article.ArticleCreatedEventPayload
import kuke.board.common.event.payload.article.ArticleDeletedEventPayload
import kuke.board.common.event.payload.article.ArticleUpdatedEventPayload
import kuke.board.common.event.payload.comment.CommentCreatedEventPayload
import kuke.board.common.event.payload.comment.CommentDeletedEventPayload
import kuke.board.common.event.payload.like.ArticleLikedEventPayload
import kuke.board.common.event.payload.like.ArticleUnlikedEventPayload
import kuke.board.common.event.payload.view.ArticleViewedEventPayload

private val log = KotlinLogging.logger {}

enum class EventType(
    val payloadClass: Class<out EventPayload>,
    val topic: String,
) {
    ARTICLE_CREATED(ArticleCreatedEventPayload::class.java, Topics.KUKE_BOARD_ARTICLE),
    ARTICLE_UPDATED(ArticleUpdatedEventPayload::class.java, Topics.KUKE_BOARD_ARTICLE),
    ARTICLE_DELETED(ArticleDeletedEventPayload::class.java, Topics.KUKE_BOARD_ARTICLE),

    COMMENT_CREATED(CommentCreatedEventPayload::class.java, Topics.KUKE_BOARD_COMMENT),
    COMMENT_DELETED(CommentDeletedEventPayload::class.java, Topics.KUKE_BOARD_COMMENT),

    ARTICLE_LIKED(ArticleLikedEventPayload::class.java, Topics.KUKE_BOARD_LIKE),
    ARTICLE_UNLIKED(ArticleUnlikedEventPayload::class.java, Topics.KUKE_BOARD_LIKE),

    ARTICLE_VIEWED(ArticleViewedEventPayload::class.java, Topics.KUKE_BOARD_VIEW);

    companion object {
        fun from(
            type: String,
        ): EventType? {
            return try {
                valueOf(type)
            } catch (
                e: Exception,
            ) {
                log.error(e) { "[EventType.from] type=$type" }
                null
            }
        }
    }
}
