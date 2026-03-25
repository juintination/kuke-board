package kuke.board.hotarticle.utils

import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

fun calculateDurationToMidnight(): Duration {
    val now = LocalDateTime.now()
    val midnight = now.plusDays(1).with(LocalTime.MIDNIGHT)
    return Duration.between(now, midnight)
}
