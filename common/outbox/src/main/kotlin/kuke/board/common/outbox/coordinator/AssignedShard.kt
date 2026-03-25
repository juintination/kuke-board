package kuke.board.common.outbox.coordinator

class AssignedShard private constructor(
    val shards: List<Long>
) {
    companion object {
        fun of(
            appId: String,
            appIds: List<String>,
            shardCount: Long,
        ): AssignedShard {
            val assigned = assign(
                appId = appId,
                appIds = appIds,
                shardCount = shardCount,
            )
            return AssignedShard(
                shards = assigned,
            )
        }

        private fun assign(
            appId: String,
            appIds: List<String>,
            shardCount: Long,
        ): List<Long> {
            val appIndex = appIds.indexOf(appId)
            if (appIndex == -1) return emptyList()

            val start = appIndex * shardCount / appIds.size
            val end = (appIndex + 1) * shardCount / appIds.size - 1
            return (start..end).map { it }
        }
    }
}
