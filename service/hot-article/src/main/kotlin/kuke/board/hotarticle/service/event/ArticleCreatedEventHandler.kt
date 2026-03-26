package kuke.board.hotarticle.service.event

import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.article.ArticleCreatedEventPayload
import kuke.board.hotarticle.repository.ArticleCreatedTimeRepository
import kuke.board.hotarticle.utils.calculateDurationToMidnight
import org.springframework.stereotype.Component

@Component
class ArticleCreatedEventHandler(
    private val articleCreatedTimeRepository: ArticleCreatedTimeRepository,
) : EventHandler<ArticleCreatedEventPayload> {

    override fun handle(
        event: Event<ArticleCreatedEventPayload>,
    ) {
        val payload = event.payload
        articleCreatedTimeRepository.createOrUpdate(
            articleId = payload.articleId,
            createdAt = payload.createdAt,
            ttl = calculateDurationToMidnight(),
        )
    }

    override fun supports(
        event: Event<ArticleCreatedEventPayload>,
    ) = event.type == EventType.ARTICLE_CREATED

    override fun findArticleId(
        event: Event<ArticleCreatedEventPayload>,
    ) = event.payload.articleId
}
