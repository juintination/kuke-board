package kuke.board.comment.service

import kuke.board.comment.dto.request.CommentCreateRequest
import kuke.board.comment.dto.request.CommentUpdateRequest
import kuke.board.comment.dto.response.CommentResponse
import kuke.board.comment.entity.Comment
import kuke.board.comment.repository.CommentRepository
import kuke.board.common.snowflake.Snowflake
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommentService(
    private val commentRepository: CommentRepository,
) {

    private val snowflake = Snowflake()

    @Transactional
    fun create(
        request: CommentCreateRequest,
    ): CommentResponse {
        val parent = findParent(request.parentId)
        val comment = Comment.create(
            id = snowflake.nextId(),
            articleId = request.articleId,
            writerId = request.writerId,
            parentId = parent?.id,
            content = request.content,
        )
        return CommentResponse.from(commentRepository.save(comment))
    }

    @Transactional(readOnly = true)
    fun read(
        commentId: Long,
    ): CommentResponse {
        val comment = getCommentOrThrow(commentId)
        return CommentResponse.from(comment)
    }

    @Transactional
    fun update(
        commentId: Long,
        request: CommentUpdateRequest,
    ): CommentResponse {
        val comment = getCommentOrThrow(commentId)
        comment.update(request)
        return CommentResponse.from(comment)
    }

    @Transactional
    fun delete(
        commentId: Long,
    ) {
        val comment = getCommentOrThrow(commentId)
        commentRepository.delete(comment)
    }

    private fun findParent(
        parentId: Long?,
    ): Comment? {
        return parentId?.let {
            commentRepository.findByIdOrNull(it)
        }
    }

    private fun getCommentOrThrow(
        commentId: Long
    ): Comment = commentRepository.findByIdOrNull(commentId)
        ?: throw IllegalArgumentException("존재하지 않는 댓글입니다. commentId: $commentId")
}
