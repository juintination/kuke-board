package kuke.board.view.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import kotlin.time.Duration
import kotlin.time.toJavaDuration

@Repository
class ArticleViewDistributedLockRepository(
    private val redisTemplate: StringRedisTemplate
) {

    companion object {
        private const val KEY_PREFIX = "view:article"
        private const val USER = "user"
        private const val LOCK = "lock"
    }

    fun tryMarkViewed(
        articleId: Long,
        userId: Long,
        ttl: Duration,
    ): Boolean {
        val key = key(
            articleId = articleId,
            userId = userId,
        )

        return redisTemplate.opsForValue()
            .setIfAbsent(key, "", ttl.toJavaDuration()) ?: false
    }

    private fun key(
        articleId: Long,
        userId: Long,
    ) = "$KEY_PREFIX:$articleId:$USER:$userId:$LOCK"
}
