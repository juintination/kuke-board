package kuke.board.common.outbox.entity

import io.hypersistence.utils.hibernate.id.Tsid
import jakarta.persistence.*
import kuke.board.common.event.EventType
import java.time.LocalDateTime

@Entity
@Table(
    name = "outbox",
    indexes = [
        Index(
            name = "idx_shard_key_created_at",
            columnList = "shard_key, created_at"
        )
    ]
)
class Outbox private constructor(

    @Id
    @Tsid
    @Column(columnDefinition = "BIGINT UNSIGNED")
    val id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val eventType: EventType,

    @Column(nullable = false, columnDefinition = "TEXT")
    val payload: String,

    @Column(nullable = false)
    val shardKey: Long,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun create(
            eventType: EventType,
            payload: String,
            shardKey: Long,
        ) = Outbox(
            eventType = eventType,
            payload = payload,
            shardKey = shardKey,
        )
    }
}
