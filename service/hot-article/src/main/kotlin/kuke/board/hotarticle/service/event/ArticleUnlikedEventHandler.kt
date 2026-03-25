package kuke.board.hotarticle.service.event

import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.like.ArticleUnlikedEventPayload
import kuke.board.hotarticle.repository.ArticleLikeCountRepository
import kuke.board.hotarticle.utils.calculateDurationToMidnight
import org.springframework.stereotype.Component

@Component
class ArticleUnlikedEventHandler(
    private val articleLikeCountRepository: ArticleLikeCountRepository,
) : EventHandler<ArticleUnlikedEventPayload> {

    override fun handle(
        event: Event<ArticleUnlikedEventPayload>,
    ) {
        val payload = event.payload
        articleLikeCountRepository.createOrUpdate(
            articleId = payload.articleId!!,
            likeCount = payload.articleLikeCount!!,
            ttl = calculateDurationToMidnight(),
        )
    }

    override fun supports(
        event: Event<ArticleUnlikedEventPayload>,
    ) = event.type == EventType.ARTICLE_UNLIKED

    override fun findArticleId(
        event: Event<ArticleUnlikedEventPayload>,
    ) = event.payload.articleId!!
}
