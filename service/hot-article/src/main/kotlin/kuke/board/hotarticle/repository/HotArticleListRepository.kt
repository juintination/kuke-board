package kuke.board.hotarticle.repository

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Repository
class HotArticleListRepository(
    private val redisTemplate: StringRedisTemplate,
) {

    companion object {
        private const val KEY_FORMAT = "hot-article:list:%s"
        private val TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd")
    }

    private val log = KotlinLogging.logger {}

    fun add(
        articleId: Long,
        time: LocalDateTime,
        score: Long,
        limit: Long,
        ttl: Duration,
    ) {
        val key = generateKey(
            time = time,
        )

        redisTemplate.opsForZSet().add(key, articleId.toString(), score.toDouble())

        val size = redisTemplate.opsForZSet().size(key) ?: 0
        if (size > limit) {
            redisTemplate.opsForZSet().removeRange(key, 0, size - limit - 1)
        }

        redisTemplate.expire(key, ttl)
    }

    fun remove(
        articleId: Long,
        time: LocalDateTime,
    ) {
        redisTemplate.opsForZSet().remove(
            generateKey(
                time = time,
            ), articleId.toString()
        )
    }

    fun readAll(
        dateStr: String,
    ): List<Long> {
        return redisTemplate.opsForZSet().reverseRangeWithScores(
            generateKey(
                dateStr = dateStr,
            ), 0, -1
        )?.mapNotNull { tuple ->
            log.info { "[HotArticleListRepository.readAll] articleId=${tuple.value}, score=${tuple.score}" }
            tuple.value?.toLong()
        } ?: emptyList()
    }

    private fun generateKey(
        time: LocalDateTime,
    ) = generateKey(TIME_FORMATTER.format(time))

    private fun generateKey(
        dateStr: String,
    ) = KEY_FORMAT.format(dateStr)
}
