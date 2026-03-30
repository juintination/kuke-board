package kuke.board.articleread.cache.model

import com.fasterxml.jackson.annotation.JsonIgnore
import kuke.board.common.serialization.DataSerializer
import java.time.Duration
import java.time.LocalDateTime

data class OptimizedCache(
    val data: String,
    val expiredAt: LocalDateTime
) {

    @JsonIgnore
    fun isExpired(): Boolean =
        LocalDateTime.now().isAfter(expiredAt)

    fun <T> parseData(
        clazz: Class<T>,
    ): T = DataSerializer.fromJson(
        data = data,
        clazz = clazz,
    )

    companion object {
        fun of(
            data: Any,
            ttl: Duration,
        ) = OptimizedCache(
            data = DataSerializer.toJson(
                obj = data,
            ),
            expiredAt = LocalDateTime.now().plus(ttl)
        )
    }
}
