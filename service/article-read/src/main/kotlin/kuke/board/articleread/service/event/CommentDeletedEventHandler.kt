package kuke.board.articleread.service.event

import kuke.board.articleread.repository.ArticleQueryModelRepository
import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.comment.CommentDeletedEventPayload
import org.springframework.stereotype.Component

@Component
class CommentDeletedEventHandler(
    private val articleQueryModelRepository: ArticleQueryModelRepository,
) : EventHandler<CommentDeletedEventPayload> {

    override fun handle(
        event: Event<CommentDeletedEventPayload>,
    ) {
        val payload = event.payload
        articleQueryModelRepository.read(
            articleId = payload.articleId,
        )?.let { articleQueryModel ->
            articleQueryModel.apply(
                payload = payload,
            )
            articleQueryModelRepository.update(
                articleQueryModel = articleQueryModel,
            )
        }
    }

    override fun supports(
        event: Event<CommentDeletedEventPayload>,
    ) = event.type == EventType.COMMENT_DELETED
}
