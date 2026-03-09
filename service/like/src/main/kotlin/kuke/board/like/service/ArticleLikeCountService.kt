package kuke.board.like.service

import kuke.board.like.entity.ArticleLikeCount
import kuke.board.like.repository.ArticleLikeCountRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleLikeCountService(
    private val articleLikeCountRepository: ArticleLikeCountRepository,
) {

    @Transactional(readOnly = true)
    fun getLikeCount(
        articleId: Long,
    ): Long {
        return articleLikeCountRepository.findById(articleId)
            .map { it.likeCount }
            .orElse(0L)
    }

    @Transactional
    fun increase(
        articleId: Long,
    ) {
        val updated = articleLikeCountRepository.increase(articleId)

        if (updated == 0) {
            articleLikeCountRepository.save(
                ArticleLikeCount.create(
                    articleId = articleId,
                )
            )
            articleLikeCountRepository.increase(articleId)
        }
    }

    @Transactional
    fun decrease(
        articleId: Long,
    ) {
        articleLikeCountRepository.decrease(articleId)
    }
}
