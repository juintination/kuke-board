package kuke.board.articleread.cache.model

import java.time.Duration

data class OptimizedCacheTTL(
    val logicalTTL: Duration,
    val physicalTTL: Duration,
) {
    companion object {
        private const val PHYSICAL_TTL_DELAY_SECONDS = 5L

        fun of(
            ttlSeconds: Long,
        ): OptimizedCacheTTL {
            val logical = Duration.ofSeconds(ttlSeconds)
            return OptimizedCacheTTL(
                logicalTTL = logical,
                physicalTTL = logical.plusSeconds(PHYSICAL_TTL_DELAY_SECONDS),
            )
        }
    }
}
