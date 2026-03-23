package kuke.board.comment.service

import kuke.board.comment.entity.ArticleCommentCount
import kuke.board.comment.repository.ArticleCommentCountRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleCommentCountService(
    private val articleCommentCountRepository: ArticleCommentCountRepository,
) {

    @Transactional(readOnly = true)
    fun getLikeCount(
        articleId: Long,
    ): Long {
        return articleCommentCountRepository.findById(articleId)
            .map { it.commentCount }
            .orElse(0L)
    }

    @Transactional
    fun increase(
        articleId: Long,
    ) {
        val updated = articleCommentCountRepository.increase(articleId)

        if (updated == 0) {
            articleCommentCountRepository.save(
                ArticleCommentCount.create(
                    articleId = articleId,
                )
            )
            articleCommentCountRepository.increase(articleId)
        }
    }

    @Transactional
    fun decrease(
        articleId: Long,
    ) {
        articleCommentCountRepository.decrease(articleId)
    }
}
