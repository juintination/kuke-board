package kuke.board.articleread.service.event

import kuke.board.articleread.repository.ArticleQueryModelRepository
import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.like.ArticleUnlikedEventPayload
import org.springframework.stereotype.Component

@Component
class ArticleUnlikedEventHandler(
    private val articleQueryModelRepository: ArticleQueryModelRepository,
) : EventHandler<ArticleUnlikedEventPayload> {

    override fun handle(
        event: Event<ArticleUnlikedEventPayload>,
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
        event: Event<ArticleUnlikedEventPayload>,
    ) = event.type == EventType.ARTICLE_UNLIKED
}
