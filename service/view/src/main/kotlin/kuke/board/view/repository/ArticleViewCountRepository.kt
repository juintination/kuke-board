package kuke.board.view.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository

@Repository
class ArticleViewCountRepository(
    private val redisTemplate: StringRedisTemplate
) {

    companion object {
        private const val KEY_PREFIX = "view:article"
        private const val COUNT = "count"
    }

    fun read(
        articleId: Long
    ): Long = redisTemplate.opsForValue()
        .get(key(articleId))
        ?.toLong()
        ?: 0L

    fun increase(
        articleId: Long
    ): Long? = redisTemplate.opsForValue()
        .increment(key(articleId))

    private fun key(
        articleId: Long
    ) = "$KEY_PREFIX:$articleId:$COUNT"
}
