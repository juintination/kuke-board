package kuke.board.articleread.repository

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.Range
import org.springframework.data.redis.connection.Limit
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository

@Repository
class ArticleIdListRepository(
    private val redisTemplate: StringRedisTemplate,
) {

    companion object {
        private const val KEY_FORMAT = "article-read:board:%s:article-list"
    }

    private fun Long.toPaddedString() =
        this.toString().padStart(19, '0')

    private val log = KotlinLogging.logger {}

    fun add(
        boardId: Long,
        articleId: Long,
        limit: Long,
    ) {
        val key = generateKey(
            boardId = boardId,
        )

        redisTemplate.opsForZSet().add(key, articleId.toPaddedString(), 0.0)

        val size = redisTemplate.opsForZSet().size(key) ?: 0
        if (size > limit) {
            redisTemplate.opsForZSet().removeRange(key, 0, size - limit - 1)
        }
    }

    fun delete(
        boardId: Long,
        articleId: Long,
    ) {
        val key = generateKey(
            boardId = boardId,
        )

        redisTemplate.opsForZSet().remove(key, articleId.toPaddedString())
    }

    fun readAll(
        boardId: Long,
        offset: Long,
        limit: Long,
    ): List<Long> {
        val key = generateKey(
            boardId = boardId,
        )

        return redisTemplate.opsForZSet()
            .reverseRange(key, offset, offset + limit - 1)
            ?.map { it.toLong() }
            ?: emptyList()
    }

    fun readAllInfiniteScroll(
        boardId: Long,
        lastArticleId: Long?,
        limit: Int,
    ): List<Long> {
        val key = generateKey(
            boardId = boardId,
        )

        val range = if (lastArticleId == null) {
            Range.unbounded()
        } else {
            Range.leftUnbounded(Range.Bound.exclusive(lastArticleId.toPaddedString()))
        }

        return redisTemplate.opsForZSet()
            .reverseRangeByLex(key, range, Limit.limit().count(limit))
            ?.map { it.toLong() }
            ?: emptyList()
    }

    private fun generateKey(
        boardId: Long,
    ) = KEY_FORMAT.format(boardId)
}
