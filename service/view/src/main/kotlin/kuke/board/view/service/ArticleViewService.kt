package kuke.board.view.service

import kuke.board.view.repository.ArticleViewCountRepository
import kuke.board.view.repository.ArticleViewDistributedLockRepository
import org.springframework.stereotype.Service
import kotlin.time.Duration.Companion.minutes

@Service
class ArticleViewService(
    private val articleViewCountRepository: ArticleViewCountRepository,
    private val articleViewDistributedLockRepository: ArticleViewDistributedLockRepository,
    private val articleViewCountBackUpProcessor: ArticleViewCountBackUpProcessor
) {

    companion object {
        private const val BACK_UP_BATCH_SIZE = 100L
        private val VIEW_LOCK_TTL = 10.minutes
    }

    fun increase(
        articleId: Long,
        userId: Long,
    ): Long {
        val isLockAcquired = articleViewDistributedLockRepository.tryMarkViewed(
            articleId = articleId,
            userId = userId,
            ttl = VIEW_LOCK_TTL,
        )

        return if (isLockAcquired) {
            val viewCount = articleViewCountRepository.increase(articleId)!!
            if (viewCount % BACK_UP_BATCH_SIZE == 0L) {
                articleViewCountBackUpProcessor.backUp(
                    articleId = articleId,
                    viewCount = viewCount,
                )
            }
            viewCount
        } else {
            articleViewCountRepository.read(articleId)
        }
    }

    fun count(
        articleId: Long,
    ) = articleViewCountRepository.read(
        articleId = articleId,
    )
}
