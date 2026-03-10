package kuke.board.view.service

import kuke.board.view.repository.ArticleViewCountRepository
import org.springframework.stereotype.Service

@Service
class ArticleViewService(
    private val articleViewCountRepository: ArticleViewCountRepository,
    private val articleViewCountBackUpProcessor: ArticleViewCountBackUpProcessor
) {

    companion object {
        private const val BACK_UP_BATCH_SIZE = 100L
    }

    fun increase(
        articleId: Long,
    ): Long {
        val viewCount = articleViewCountRepository.increase(articleId)!!

        if (viewCount % BACK_UP_BATCH_SIZE == 0L) {
            articleViewCountBackUpProcessor.backUp(
                articleId = articleId,
                viewCount = viewCount,
            )
        }

        return viewCount
    }

    fun count(
        articleId: Long,
    ) = articleViewCountRepository.read(
        articleId = articleId,
    )
}
