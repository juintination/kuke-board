package kuke.board.articleread.cache.aspect

import kuke.board.articleread.cache.annotation.OptimizedCacheable
import kuke.board.articleread.cache.manager.OptimizedCacheManager
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

@Aspect
@Component
class OptimizedCacheAspect(
    private val optimizedCacheManager: OptimizedCacheManager,
) {

    @Around("@annotation(kuke.board.articleread.cache.annotation.OptimizedCacheable)")
    fun around(
        joinPoint: ProceedingJoinPoint,
    ): Any? {
        val methodSignature = joinPoint.signature as MethodSignature
        val cacheable = methodSignature.method.getAnnotation(OptimizedCacheable::class.java)

        return optimizedCacheManager.process(
            type = cacheable.type,
            ttlSeconds = cacheable.ttlSeconds,
            args = joinPoint.args,
            clazz = methodSignature.returnType,
            originDataSupplier = { joinPoint.proceed() }
        )
    }
}
