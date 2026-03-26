package kuke.board.articleread.repository

import io.github.oshai.kotlinlogging.KotlinLogging
import kuke.board.articleread.model.ArticleQueryModel
import kuke.board.common.serialization.DataSerializer
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class ArticleQueryModelRepository(
    private val redisTemplate: StringRedisTemplate,
) {

    companion object {
        private const val KEY_FORMAT = "article-read:article:%s"
    }

    private val log = KotlinLogging.logger {}

    fun create(
        articleQueryModel: ArticleQueryModel,
        ttl: Duration,
    ) {
        val key = generateKey(
            articleQueryModel = articleQueryModel,
        )

        redisTemplate.opsForValue()
            .set(key, DataSerializer.toJson(articleQueryModel)!!, ttl)
    }

    fun update(
        articleQueryModel: ArticleQueryModel,
    ) {
        val key = generateKey(
            articleQueryModel = articleQueryModel,
        )

        redisTemplate.opsForValue()
            .setIfPresent(key, DataSerializer.toJson(articleQueryModel)!!)
    }

    fun delete(
        articleId: Long,
    ) {
        val key = generateKey(
            articleId = articleId,
        )

        redisTemplate.delete(key)
    }

    fun read(
        articleId: Long,
    ): ArticleQueryModel? {
        val key = generateKey(
            articleId = articleId,
        )

        val json = redisTemplate.opsForValue().get(key) ?: return null
        return try {
            DataSerializer.fromJson(json, ArticleQueryModel::class.java)
        } catch (e: Exception) {
            log.error(e) { "[ArticleQueryModelRepository.read] articleId=$articleId" }
            null
        }
    }

    private fun generateKey(
        articleQueryModel: ArticleQueryModel,
    ) = generateKey(articleQueryModel.id)

    private fun generateKey(
        articleId: Long,
    ) = KEY_FORMAT.format(articleId)
}
