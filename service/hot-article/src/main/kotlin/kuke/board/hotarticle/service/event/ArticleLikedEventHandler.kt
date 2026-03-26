package kuke.board.hotarticle.service.event

import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.like.ArticleLikedEventPayload
import kuke.board.hotarticle.repository.ArticleLikeCountRepository
import kuke.board.hotarticle.utils.calculateDurationToMidnight
import org.springframework.stereotype.Component

@Component
class ArticleLikedEventHandler(
    private val articleLikeCountRepository: ArticleLikeCountRepository,
) : EventHandler<ArticleLikedEventPayload> {

    override fun handle(
        event: Event<ArticleLikedEventPayload>,
    ) {
        val payload = event.payload
        articleLikeCountRepository.createOrUpdate(
            articleId = payload.articleId,
            likeCount = payload.articleLikeCount,
            ttl = calculateDurationToMidnight(),
        )
    }

    override fun supports(
        event: Event<ArticleLikedEventPayload>,
    ) = event.type == EventType.ARTICLE_LIKED

    override fun findArticleId(
        event: Event<ArticleLikedEventPayload>,
    ) = event.payload.articleId
}
