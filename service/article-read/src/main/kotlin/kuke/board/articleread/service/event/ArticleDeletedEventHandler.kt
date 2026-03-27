package kuke.board.articleread.service.event

import kuke.board.articleread.repository.ArticleIdListRepository
import kuke.board.articleread.repository.ArticleQueryModelRepository
import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.article.ArticleDeletedEventPayload
import org.springframework.stereotype.Component

@Component
class ArticleDeletedEventHandler(
    private val articleQueryModelRepository: ArticleQueryModelRepository,
    private val articleIdListRepository: ArticleIdListRepository,
) : EventHandler<ArticleDeletedEventPayload> {

    override fun handle(
        event: Event<ArticleDeletedEventPayload>,
    ) {
        val payload = event.payload
        articleIdListRepository.delete(
            boardId = payload.boardId,
            articleId = payload.articleId,
        )
        articleQueryModelRepository.delete(
            articleId = payload.articleId,
        )
    }

    override fun supports(
        event: Event<ArticleDeletedEventPayload>,
    ) = event.type == EventType.ARTICLE_DELETED
}
