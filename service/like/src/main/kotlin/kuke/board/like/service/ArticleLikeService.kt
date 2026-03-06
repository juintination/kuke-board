package kuke.board.like.service

import kuke.board.common.snowflake.Snowflake
import kuke.board.like.dto.response.ArticleLikeResponse
import kuke.board.like.entity.ArticleLike
import kuke.board.like.repository.ArticleLikeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleLikeService(
    private val articleLikeRepository: ArticleLikeRepository
) {

    private val snowflake = Snowflake()

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
            } else {
                existingLike.tombstone()
            }
            existingLike
        } else {
            articleLikeRepository.save(
                ArticleLike.create(
                    id = snowflake.nextId(),
                    articleId = articleId,
                    userId = userId,
                )
            )
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
