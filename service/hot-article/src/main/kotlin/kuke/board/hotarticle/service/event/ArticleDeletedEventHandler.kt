package kuke.board.hotarticle.service.event

import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.article.ArticleDeletedEventPayload
import kuke.board.hotarticle.repository.ArticleCreatedTimeRepository
import kuke.board.hotarticle.repository.HotArticleListRepository
import org.springframework.stereotype.Component

@Component
class ArticleDeletedEventHandler(
    private val hotArticleListRepository: HotArticleListRepository,
    private val articleCreatedTimeRepository: ArticleCreatedTimeRepository,
) : EventHandler<ArticleDeletedEventPayload> {

    override fun handle(
        event: Event<ArticleDeletedEventPayload>,
    ) {
        val payload = event.payload
        articleCreatedTimeRepository.delete(
            articleId = payload.articleId,
        )

        hotArticleListRepository.remove(
            articleId = payload.articleId,
            time = payload.createdAt,
        )
    }

    override fun supports(
        event: Event<ArticleDeletedEventPayload>,
    ) = event.type == EventType.ARTICLE_DELETED

    override fun findArticleId(
        event: Event<ArticleDeletedEventPayload>,
    ) = event.payload.articleId
}
