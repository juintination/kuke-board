package kuke.board.articleread.cache.manager

import kuke.board.articleread.cache.lock.OptimizedCacheLockProvider
import kuke.board.articleread.cache.model.OptimizedCache
import kuke.board.articleread.cache.model.OptimizedCacheTTL
import kuke.board.common.serialization.DataSerializer
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class OptimizedCacheManager(
    private val redisTemplate: StringRedisTemplate,
    private val optimizedCacheLockProvider: OptimizedCacheLockProvider,
) {
    companion object {
        private const val DELIMITER = ":"
    }

    fun <T : Any> process(
        type: String,
        ttlSeconds: Long,
        args: Array<Any>,
        clazz: Class<T>,
        originDataSupplier: () -> T?,
    ): T? {
        val key = generateKey(
            prefix = type,
            args = args,
        )

        val cachedData = redisTemplate.opsForValue().get(key)
            ?: return refresh(
                originDataSupplier = originDataSupplier,
                key = key,
                ttlSeconds = ttlSeconds,
            )

        val optimizedCache = DataSerializer.fromJson(
            data = cachedData,
            clazz = OptimizedCache::class.java,
        )

        if (!optimizedCache.isExpired()) {
            return optimizedCache.parseData(
                clazz = clazz,
            )
        }

        if (!optimizedCacheLockProvider.lock(key)) {
            return optimizedCache.parseData(
                clazz = clazz,
            )
        }

        return try {
            refresh(
                originDataSupplier = originDataSupplier,
                key = key,
                ttlSeconds = ttlSeconds,
            )
        } finally {
            optimizedCacheLockProvider.unlock(
                key = key,
            )
        }
    }

    private fun <T : Any> refresh(
        originDataSupplier: () -> T?,
        key: String,
        ttlSeconds: Long,
    ): T? {
        val result = originDataSupplier()

        val ttl = OptimizedCacheTTL.Companion.of(ttlSeconds)

        if (result != null) {
            val optimizedCache = OptimizedCache.Companion.of(
                data = result,
                ttl = ttl.logicalTTL
            )

            redisTemplate.opsForValue().set(key, DataSerializer.toJson(optimizedCache), ttl.physicalTTL)
        }

        return result
    }

    private fun generateKey(
        prefix: String,
        args: Array<Any>,
    ) = buildString {
        append(prefix)
        append(DELIMITER)
        append(args.joinToString(DELIMITER) { it.toString() })
    }
}
