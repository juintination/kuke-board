package kuke.board.articleread.config

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import java.time.Duration

@Configuration
@EnableCaching
class CacheConfig {

    companion object {
        const val ARTICLE_VIEW_COUNT_CACHE = "articleViewCount"
        val VIEW_COUNT_TTL: Duration = Duration.ofSeconds(1)
    }

    @Bean
    fun cacheManager(
        connectionFactory: RedisConnectionFactory,
    ): RedisCacheManager = RedisCacheManager.builder(connectionFactory)
        .withInitialCacheConfigurations(
            mapOf(
                ARTICLE_VIEW_COUNT_CACHE to
                        RedisCacheConfiguration.defaultCacheConfig()
                            .entryTtl(VIEW_COUNT_TTL),
            ),
        )
        .build()
}
