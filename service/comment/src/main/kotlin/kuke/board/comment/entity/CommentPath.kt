package kuke.board.comment.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class CommentPath protected constructor(
    @Column(name = "path", length = 255, nullable = false)
    var path: String = "",
) {
    companion object {
        private const val CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

        private const val DEPTH_CHUNK_SIZE = 5
        private const val MAX_DEPTH = 64

        private val MIN_CHUNK: String = CHARSET[0].toString().repeat(DEPTH_CHUNK_SIZE)
        private val MAX_CHUNK: String = CHARSET[CHARSET.length - 1].toString().repeat(DEPTH_CHUNK_SIZE)

        fun create(
            path: String,
        ): CommentPath {
            if (isDepthOverflowed(path)) {
                throw IllegalStateException("depth overflowed. path=$path")
            }
            return CommentPath(path)
        }

        private fun isDepthOverflowed(
            path: String,
        ): Boolean = calDepth(path) > MAX_DEPTH

        private fun calDepth(
            path: String,
        ): Int = path.length / DEPTH_CHUNK_SIZE
    }

    /**
     * 현재 path(부모 path)의 "다음 자식 path"를 생성한다.
     *
     * @param descendantsTopPath 부모 subtree에서 가장 큰 path(없으면 null)
     *
     * - 자식이 아직 없으면: parent + MIN_CHUNK
     * - 자식이 있으면: "부모의 직계 자식 레벨 chunk"를 뽑아서 +1
     */
    fun createChildPath(
        descendantsTopPath: String?,
    ): CommentPath {
        if (descendantsTopPath == null) {
            return create(path + MIN_CHUNK)
        }
        val childrenTopPath = findChildrenTopPath(descendantsTopPath)
        return create(increase(childrenTopPath))
    }

    private fun findChildrenTopPath(
        descendantsTopPath: String,
    ): String {
        val nextDepthEnd = (calDepth(path) + 1) * DEPTH_CHUNK_SIZE
        return descendantsTopPath.substring(0, nextDepthEnd)
    }

    private fun increase(
        targetPath: String,
    ): String {
        val lastChunk = targetPath.substring(targetPath.length - DEPTH_CHUNK_SIZE)
        if (lastChunk == MAX_CHUNK) {
            throw IllegalStateException("chunk overflowed. path=$targetPath")
        }

        val charsetLength = CHARSET.length
        var value = 0
        for (ch in lastChunk) {
            value = value * charsetLength + CHARSET.indexOf(ch)
        }
        value += 1

        val sb = StringBuilder()
        var x = value
        repeat(DEPTH_CHUNK_SIZE) {
            sb.append(CHARSET[x % charsetLength])
            x /= charsetLength
        }
        val newChunk = sb.reverse().toString()

        return targetPath.substring(0, targetPath.length - DEPTH_CHUNK_SIZE) + newChunk
    }
}
