package kuke.board.articleread.service.event

import kuke.board.articleread.repository.ArticleQueryModelRepository
import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.comment.CommentCreatedEventPayload
import org.springframework.stereotype.Component

@Component
class CommentCreatedEventHandler(
    private val articleQueryModelRepository: ArticleQueryModelRepository,
) : EventHandler<CommentCreatedEventPayload> {

    override fun handle(
        event: Event<CommentCreatedEventPayload>,
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
        event: Event<CommentCreatedEventPayload>,
    ) = event.type == EventType.COMMENT_CREATED
}
