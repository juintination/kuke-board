package kuke.board.hotarticle.service.event

import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.view.ArticleViewedEventPayload
import kuke.board.hotarticle.repository.ArticleViewCountRepository
import kuke.board.hotarticle.utils.calculateDurationToMidnight
import org.springframework.stereotype.Component

@Component
class ArticleViewedEventHandler(
    private val articleViewCountRepository: ArticleViewCountRepository,
) : EventHandler<ArticleViewedEventPayload> {

    override fun handle(
        event: Event<ArticleViewedEventPayload>,
    ) {
        val payload = event.payload
        articleViewCountRepository.createOrUpdate(
            articleId = payload.articleId,
            viewCount = payload.articleViewCount,
            ttl = calculateDurationToMidnight(),
        )
    }

    override fun supports(
        event: Event<ArticleViewedEventPayload>,
    ) = event.type == EventType.ARTICLE_VIEWED

    override fun findArticleId(
        event: Event<ArticleViewedEventPayload>,
    ) = event.payload.articleId
}
