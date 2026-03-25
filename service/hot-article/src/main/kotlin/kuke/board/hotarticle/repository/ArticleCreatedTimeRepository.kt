package kuke.board.hotarticle.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Repository
class ArticleCreatedTimeRepository(
    private val redisTemplate: StringRedisTemplate
) {

    fun createOrUpdate(
        articleId: Long,
        createdAt: LocalDateTime,
        ttl: Duration,
    ) {
        val key = key(
            articleId = articleId,
        )

        redisTemplate.opsForValue()
            .set(key, createdAt.toInstant(ZoneOffset.UTC).toEpochMilli().toString(), ttl)
    }

    fun delete(
        articleId: Long,
    ) {
        val key = key(
            articleId = articleId,
        )

        redisTemplate.delete(key)
    }

    fun read(
        articleId: Long,
    ): LocalDateTime? {
        val key = key(
            articleId = articleId,
        )

        val result = redisTemplate.opsForValue()
            .get(key) ?: return null

        return LocalDateTime.ofInstant(
            Instant.ofEpochMilli(result.toLong()),
            ZoneOffset.UTC
        )
    }

    private fun key(
        articleId: Long,
    ) = "hot-article:article:$articleId:created-time"
}
