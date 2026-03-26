package kuke.board.articleread.service.event

import kuke.board.articleread.repository.ArticleQueryModelRepository
import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.article.ArticleUpdatedEventPayload
import org.springframework.stereotype.Component

@Component
class ArticleUpdatedEventHandler(
    private val articleQueryModelRepository: ArticleQueryModelRepository,
) : EventHandler<ArticleUpdatedEventPayload> {

    override fun handle(
        event: Event<ArticleUpdatedEventPayload>,
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
        event: Event<ArticleUpdatedEventPayload>,
    ) = event.type == EventType.ARTICLE_UPDATED
}
