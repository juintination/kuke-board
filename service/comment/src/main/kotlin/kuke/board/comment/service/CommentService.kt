package kuke.board.comment.service

import kuke.board.comment.dto.request.CommentCreateRequest
import kuke.board.comment.dto.request.CommentCursorRequest
import kuke.board.comment.dto.request.CommentUpdateRequest
import kuke.board.comment.dto.response.CommentListResponse
import kuke.board.comment.dto.response.CommentResponse
import kuke.board.comment.entity.Comment
import kuke.board.comment.entity.CommentPath
import kuke.board.comment.repository.CommentRepository
import kuke.board.common.event.EventType
import kuke.board.common.event.payload.comment.CommentCreatedEventPayload
import kuke.board.common.event.payload.comment.CommentDeletedEventPayload
import kuke.board.common.outbox.event.OutboxEventPublisher
import kuke.board.common.pagination.dto.response.CommonCursorResponse
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val articleCommentCountService: ArticleCommentCountService,
    private val outboxEventPublisher: OutboxEventPublisher,
) {

    @Transactional
    fun create(
        request: CommentCreateRequest,
    ): CommentResponse {
        val parent = findParent(
            articleId = request.articleId,
            parentId = request.parentId,
        )
        val parentCommentPath = parent?.let {
            CommentPath.create(it.path.path)
        } ?: CommentPath.create("")

        val comment = commentRepository.save(
            Comment.create(
                request = request,
                path = parentCommentPath.createChildPath(
                    findDescendantsTopPath(
                        articleId = request.articleId,
                        prefix = parentCommentPath.path,
                    )
                )
            )
        )

        articleCommentCountService.increase(
            articleId = request.articleId,
        )

        outboxEventPublisher.publish(
            eventType = EventType.COMMENT_CREATED,
            payload = CommentCreatedEventPayload(
                commentId = comment.id!!,
                content = comment.content,
                path = comment.path.path,
                parentId = comment.parentId,
                articleId = comment.articleId,
                writerId = comment.writerId,
                isTombstoned = comment.isTombstoned(),
                createdAt = comment.createdAt,
                modifiedAt = comment.modifiedAt,
                articleCommentCount = articleCommentCountService.getCommentCount(
                    articleId = comment.articleId,
                ),
            ),
            shardKey = comment.articleId,
        )

        return CommentResponse.from(comment)
    }

    @Transactional(readOnly = true)
    fun readAllCursor(
        request: CommentCursorRequest,
    ): CommonCursorResponse<CommentListResponse> {

        val pageable = PageRequest.of(
            0,
            request.size + 1
        )

        val fetched = if (request.parentId == null) {
            commentRepository.findAllRootByCursor(
                articleId = request.articleId,
                lastId = request.cursor,
                pageable = pageable,
            )
        } else {
            validateParent(
                articleId = request.articleId,
                parentId = request.parentId,
            )

            commentRepository.findAllChildByCursor(
                articleId = request.articleId,
                parentId = request.parentId,
                lastId = request.cursor,
                pageable = pageable,
            )
        }

        val hasNext = fetched.size > request.size
        val pageItems = fetched.take(request.size)

        val commentIds = pageItems.map { it.id!! }
        val parentIdsWithChildren = if (commentIds.isEmpty()) {
            emptySet()
        } else {
            commentRepository.findExistingParentIds(commentIds).toSet()
        }

        val items = pageItems.map { comment ->
            CommentListResponse.from(
                comment = comment,
                hasChildren = comment.id in parentIdsWithChildren,
            )
        }

        val nextCursor = if (hasNext) pageItems.last().id else null

        return CommonCursorResponse.of(
            items = items,
            nextCursorId = nextCursor,
            hasNext = hasNext,
        )
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

    /**
     * 삭제 정책
     * - 자식이 없는 댓글: 완전 삭제
     * - 자식이 있는 댓글: 내용 삭제 + tombstone 처리
     * - tombstone 처리된 댓글은 자식이 없어질 때까지 남아있다가, 자식이 모두 삭제되면 완전 삭제
     */
    @Transactional
    fun delete(
        commentId: Long,
    ) {
        val comment = getCommentOrThrow(commentId)

        // 자식이 있으면 tombstone 처리
        if (commentRepository.existsByParentId(commentId)) {
            comment.tombstone()
            return
        }

        commentRepository.delete(comment)
        cascadeDeleteUpIfNeeded(
            comment = comment,
        )
    }

    private fun findParent(
        articleId: Long,
        parentId: Long?,
    ): Comment? {
        if (parentId == null) {
            return null
        }

        val parent = commentRepository.findByIdOrNull(parentId)
            ?: throw IllegalArgumentException("존재하지 않는 부모 댓글입니다. parentId: $parentId")

        if (parent.articleId != articleId) {
            throw IllegalArgumentException("부모 댓글의 게시글 Id가 일치하지 않습니다. parentId: $parentId")
        }

        if (parent.isTombstoned()) {
            throw IllegalArgumentException("삭제된 댓글에는 답글을 달 수 없습니다. parentId: $parentId")
        }

        return parent
    }

    private fun validateParent(
        articleId: Long,
        parentId: Long,
    ) {
        val parent = commentRepository.findByIdOrNull(parentId)
            ?: throw IllegalArgumentException("존재하지 않는 부모 댓글입니다. parentId: $parentId")

        if (parent.articleId != articleId) {
            throw IllegalArgumentException("부모 댓글의 articleId가 일치하지 않습니다. parentId: $parentId")
        }
    }

    private fun cascadeDeleteUpIfNeeded(
        comment: Comment
    ) {
        articleCommentCountService.decrease(
            articleId = comment.articleId,
        )

        outboxEventPublisher.publish(
            eventType = EventType.COMMENT_DELETED,
            payload = CommentDeletedEventPayload(
                commentId = comment.id!!,
                content = comment.content,
                path = comment.path.path,
                parentId = comment.parentId,
                articleId = comment.articleId,
                writerId = comment.writerId,
                isTombstoned = comment.isTombstoned(),
                createdAt = comment.createdAt,
                modifiedAt = comment.modifiedAt,
                articleCommentCount = articleCommentCountService.getCommentCount(
                    articleId = comment.articleId,
                ),
            ),
            shardKey = comment.articleId,
        )

        // root면 종료
        val parentId = comment.parentId ?: return
        val parentComment = commentRepository.findByIdOrNull(parentId) ?: return

        // parent가 tombstone이 아니면 정리 대상 아님
        if (!parentComment.isTombstoned()) return

        // parent에게 남아있는 자식이 있으면 정리 불가
        if (commentRepository.existsByParentId(parentComment.id!!)) return

        // parent를 삭제 처리하고, 위로 재귀
        commentRepository.delete(parentComment)
        cascadeDeleteUpIfNeeded(parentComment)
    }

    private fun getCommentOrThrow(
        commentId: Long
    ): Comment = commentRepository.findByIdOrNull(commentId)
        ?: throw IllegalArgumentException("존재하지 않는 댓글입니다. commentId: $commentId")

    private fun findDescendantsTopPath(
        articleId: Long,
        prefix: String,
    ): String? {
        return commentRepository.findTopPathByPrefix(
            articleId = articleId,
            prefix = prefix,
            pageable = PageRequest.of(0, 1)
        ).firstOrNull()
    }
}
