package kuke.board.hotarticle.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class ArticleCommentCountRepository(
    private val redisTemplate: StringRedisTemplate,
) {

    fun createOrUpdate(
        articleId: Long,
        commentCount: Long,
        ttl: Duration,
    ) {
        val key = key(
            articleId = articleId,
        )

        redisTemplate.opsForValue()
            .set(key, commentCount.toString(), ttl)
    }

    fun read(
        articleId: Long,
    ): Long {
        val key = key(
            articleId = articleId,
        )

        return redisTemplate.opsForValue().get(key)?.toLong() ?: 0L
    }

    private fun key(
        articleId: Long,
    ) = "hot-article:article:$articleId:comment-count"
}
