package kuke.board.view.service

import kuke.board.common.event.EventType
import kuke.board.common.event.payload.view.ArticleViewedEventPayload
import kuke.board.common.outbox.event.OutboxEventPublisher
import kuke.board.view.entity.ArticleViewCount
import kuke.board.view.repository.ArticleViewCountBackUpRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ArticleViewCountBackUpProcessor(
    private val articleViewCountBackUpRepository: ArticleViewCountBackUpRepository,
    private val outboxEventPublisher: OutboxEventPublisher,
) {

    @Transactional
    fun backUp(
        articleId: Long,
        viewCount: Long,
    ) {
        val result = articleViewCountBackUpRepository.updateViewCount(
            articleId = articleId,
            viewCount = viewCount,
        )

        if (result == 0) {
            val exists = articleViewCountBackUpRepository.existsById(articleId)

            if (!exists) {
                articleViewCountBackUpRepository.save(
                    ArticleViewCount.create(
                        articleId = articleId,
                        viewCount = viewCount,
                    )
                )
            }
        }

        outboxEventPublisher.publish(
            eventType = EventType.ARTICLE_VIEWED,
            payload = ArticleViewedEventPayload(
                articleId = articleId,
                articleViewCount = viewCount,
            ),
            shardKey = articleId,
        )
    }
}
