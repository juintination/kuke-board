package kuke.board.hotarticle.service.event

import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.comment.CommentCreatedEventPayload
import kuke.board.hotarticle.repository.ArticleCommentCountRepository
import kuke.board.hotarticle.utils.calculateDurationToMidnight
import org.springframework.stereotype.Component

@Component
class CommentCreatedEventHandler(
    private val articleCommentCountRepository: ArticleCommentCountRepository,
) : EventHandler<CommentCreatedEventPayload> {

    override fun handle(
        event: Event<CommentCreatedEventPayload>,
    ) {
        val payload = event.payload
        articleCommentCountRepository.createOrUpdate(
            articleId = payload.articleId,
            commentCount = payload.articleCommentCount,
            ttl = calculateDurationToMidnight(),
        )
    }

    override fun supports(
        event: Event<CommentCreatedEventPayload>,
    ) = event.type == EventType.COMMENT_CREATED

    override fun findArticleId(
        event: Event<CommentCreatedEventPayload>,
    ) = event.payload.articleId
}
