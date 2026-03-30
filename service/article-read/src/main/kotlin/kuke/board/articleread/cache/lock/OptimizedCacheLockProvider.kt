package kuke.board.articleread.cache.lock

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class OptimizedCacheLockProvider(
    private val redisTemplate: StringRedisTemplate,
) {

    companion object {
        private const val KEY_PREFIX = "optimized-cache-lock:"
        private val LOCK_TTL: Duration = Duration.ofSeconds(3)
    }

    fun lock(
        key: String,
    ): Boolean {
        val lockKey = generateLockKey(
            key = key,
        )

        return redisTemplate.opsForValue()
            .setIfAbsent(lockKey, "", LOCK_TTL) == true
    }


    fun unlock(
        key: String,
        value: String,
    ) {
        val lockKey = generateLockKey(
            key = key,
        )

        val currentValue = redisTemplate.opsForValue().get(lockKey)

        if (currentValue == value) {
            redisTemplate.delete(lockKey)
        }
    }

    fun unlock(
        key: String,
    ) {
        val lockKey = generateLockKey(
            key = key,
        )

        redisTemplate.delete(lockKey)
    }

    private fun generateLockKey(
        key: String,
    ): String = "$KEY_PREFIX$key"
}
