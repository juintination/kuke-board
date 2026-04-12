package kuke.board.comment.controller

import kuke.board.comment.dto.request.CommentCreateRequest
import kuke.board.comment.dto.request.CommentCursorRequest
import kuke.board.comment.dto.request.CommentUpdateRequest
import kuke.board.comment.dto.response.CommentListResponse
import kuke.board.comment.dto.response.CommentResponse
import kuke.board.comment.service.CommentService
import kuke.board.common.pagination.dto.response.CommonCursorResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/comments")
class CommentController(
    private val commentService: CommentService,
) {

    @PostMapping
    fun create(
        @RequestBody request: CommentCreateRequest,
        @RequestHeader("X-User-Id") userId: Long,
    ): CommentResponse {
        return commentService.create(userId, request)
    }

    @GetMapping("/{commentId}")
    fun read(
        @PathVariable commentId: Long
    ): CommentResponse {
        return commentService.read(commentId)
    }

    @GetMapping("/cursor")
    fun readAllCursor(
        request: CommentCursorRequest,
    ): CommonCursorResponse<CommentListResponse> {
        return commentService.readAllCursor(request)
    }

    @PutMapping("/{commentId}")
    fun update(
        @PathVariable commentId: Long,
        @RequestBody request: CommentUpdateRequest,
        @RequestHeader("X-User-Id") userId: Long,
    ): CommentResponse {
        return commentService.update(userId, commentId, request)
    }

    @DeleteMapping("/{commentId}")
    fun delete(
        @PathVariable commentId: Long,
        @RequestHeader("X-User-Id") userId: Long,
    ) {
        commentService.delete(userId, commentId)
    }
}
