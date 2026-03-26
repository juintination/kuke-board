package kuke.board.like.service

import kuke.board.common.event.EventType
import kuke.board.common.event.payload.like.ArticleLikedEventPayload
import kuke.board.common.event.payload.like.ArticleUnlikedEventPayload
import kuke.board.common.outbox.event.OutboxEventPublisher
import kuke.board.like.dto.response.ArticleLikeResponse
import kuke.board.like.entity.ArticleLike
import kuke.board.like.repository.ArticleLikeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleLikeService(
    private val articleLikeRepository: ArticleLikeRepository,
    private val articleLikeCountService: ArticleLikeCountService,
    private val outboxEventPublisher: OutboxEventPublisher,
) {

    @Transactional
    fun toggle(
        articleId: Long,
        userId: Long,
    ): ArticleLikeResponse {
        val existingLike = articleLikeRepository.findByArticleIdAndUserId(
            articleId = articleId,
            userId = userId,
        )

        val articleLike = if (existingLike != null) {
            if (existingLike.isTombstoned()) {
                existingLike.restore()
                articleLikeCountService.increase(
                    articleId = articleId,
                )

                outboxEventPublisher.publish(
                    eventType = EventType.ARTICLE_LIKED,
                    payload = ArticleLikedEventPayload(
                        articleLikeId = existingLike.id!!,
                        articleId = articleId,
                        userId = userId,
                        createdAt = existingLike.createdAt,
                        articleLikeCount = articleLikeCountService.getLikeCount(
                            articleId = articleId,
                        ),
                    ),
                    shardKey = articleId,
                )
            } else {
                existingLike.tombstone()
                articleLikeCountService.decrease(
                    articleId = articleId,
                )

                outboxEventPublisher.publish(
                    eventType = EventType.ARTICLE_UNLIKED,
                    payload = ArticleUnlikedEventPayload(
                        articleLikeId = existingLike.id!!,
                        articleId = articleId,
                        userId = userId,
                        createdAt = existingLike.createdAt,
                        articleLikeCount = articleLikeCountService.getLikeCount(
                            articleId = articleId,
                        ),
                    ),
                    shardKey = articleId,
                )
            }
            existingLike
        } else {
            val newLike = articleLikeRepository.save(
                ArticleLike.create(
                    articleId = articleId,
                    userId = userId,
                )
            )
            articleLikeCountService.increase(
                articleId = articleId,
            )

            outboxEventPublisher.publish(
                eventType = EventType.ARTICLE_LIKED,
                payload = ArticleLikedEventPayload(
                    articleLikeId = newLike.id!!,
                    articleId = articleId,
                    userId = userId,
                    createdAt = newLike.createdAt,
                    articleLikeCount = articleLikeCountService.getLikeCount(
                        articleId = articleId,
                    ),
                ),
                shardKey = articleId,
            )
            newLike
        }

        return ArticleLikeResponse.from(articleLike)
    }

    @Transactional(readOnly = true)
    fun isLiked(
        articleId: Long,
        userId: Long,
    ): Boolean {
        return articleLikeRepository.existsByArticleIdAndUserIdAndTombstonedAtIsNull(
            articleId = articleId,
            userId = userId,
        )
    }
}
