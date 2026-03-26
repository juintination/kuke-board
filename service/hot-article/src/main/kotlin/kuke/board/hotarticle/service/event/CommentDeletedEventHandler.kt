package kuke.board.hotarticle.service.event

import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.comment.CommentDeletedEventPayload
import kuke.board.hotarticle.repository.ArticleCommentCountRepository
import kuke.board.hotarticle.utils.calculateDurationToMidnight
import org.springframework.stereotype.Component

@Component
class CommentDeletedEventHandler(
    private val articleCommentCountRepository: ArticleCommentCountRepository,
) : EventHandler<CommentDeletedEventPayload> {

    override fun handle(
        event: Event<CommentDeletedEventPayload>,
    ) {
        val payload = event.payload
        articleCommentCountRepository.createOrUpdate(
            articleId = payload.articleId,
            commentCount = payload.articleCommentCount,
            ttl = calculateDurationToMidnight(),
        )
    }

    override fun supports(
        event: Event<CommentDeletedEventPayload>,
    ) = event.type == EventType.COMMENT_DELETED

    override fun findArticleId(
        event: Event<CommentDeletedEventPayload>,
    ) = event.payload.articleId!!
}
