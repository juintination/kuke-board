package kuke.board.common.outbox.coordinator

import jakarta.annotation.PreDestroy
import kuke.board.common.outbox.enums.MessageRelayConstants
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

@Component
class MessageRelayCoordinator(
    private val redisTemplate: StringRedisTemplate,
) {

    @Value("\${spring.application.name}")
    private lateinit var applicationName: String

    private val appId: String = UUID.randomUUID().toString()

    companion object {
        const val PING_INTERVAL_SECONDS = 3L
        const val PING_FAILURE_THRESHOLD = 3L
    }

    fun assignShards() =
        AssignedShard.of(appId, findAppIds(), MessageRelayConstants.SHARD_COUNT)

    private fun findAppIds(): List<String> =
        redisTemplate.opsForZSet()
            .reverseRange(generateKey(), 0, -1)
            ?.sorted()
            ?.toList()
            ?: emptyList()

    @Scheduled(fixedDelay = PING_INTERVAL_SECONDS * 1000)
    fun ping() {
        redisTemplate.executePipelined {
            val key = generateKey()
            val now = Instant.now().toEpochMilli().toDouble()
            val expireTime = Instant.now()
                .minusSeconds(PING_INTERVAL_SECONDS * PING_FAILURE_THRESHOLD)
                .toEpochMilli().toDouble()

            redisTemplate.opsForZSet().add(key, appId, now)
            redisTemplate.opsForZSet().removeRangeByScore(key, Double.NEGATIVE_INFINITY, expireTime)
            null
        }
    }

    @PreDestroy
    fun leave() {
        redisTemplate.opsForZSet().remove(generateKey(), appId)
    }

    private fun generateKey() =
        "message-relay-coordinator:app-list:$applicationName"
}
