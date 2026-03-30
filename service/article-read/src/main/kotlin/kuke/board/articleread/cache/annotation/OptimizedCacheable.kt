package kuke.board.articleread.cache.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OptimizedCacheable(
    val type: String,
    val ttlSeconds: Long,
)
