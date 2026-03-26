package kuke.board.articleread.service.event

import kuke.board.articleread.repository.ArticleQueryModelRepository
import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.view.ArticleViewedEventPayload
import org.springframework.stereotype.Component

@Component
class ArticleViewedEventHandler(
    private val articleQueryModelRepository: ArticleQueryModelRepository,
) : EventHandler<ArticleViewedEventPayload> {

    override fun handle(
        event: Event<ArticleViewedEventPayload>,
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
        event: Event<ArticleViewedEventPayload>,
    ) = event.type == EventType.ARTICLE_VIEWED
}
