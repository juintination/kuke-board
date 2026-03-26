package kuke.board.articleread.service.event

import kuke.board.articleread.model.ArticleQueryModel
import kuke.board.articleread.repository.ArticleQueryModelRepository
import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.article.ArticleCreatedEventPayload
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class ArticleCreatedEventHandler(
    private val articleQueryModelRepository: ArticleQueryModelRepository,
) : EventHandler<ArticleCreatedEventPayload> {

    override fun handle(
        event: Event<ArticleCreatedEventPayload>,
    ) {
        val payload = event.payload
        articleQueryModelRepository.create(
            articleQueryModel = ArticleQueryModel.create(
                payload = payload,
            ),
            ttl = Duration.ofDays(1),
        )
    }

    override fun supports(
        event: Event<ArticleCreatedEventPayload>,
    ) = event.type == EventType.ARTICLE_CREATED
}
