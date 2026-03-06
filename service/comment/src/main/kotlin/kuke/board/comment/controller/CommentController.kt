package kuke.board.comment.controller

import kuke.board.comment.dto.request.CommentCreateRequest
import kuke.board.comment.dto.request.CommentCursorRequest
import kuke.board.comment.dto.request.CommentUpdateRequest
import kuke.board.comment.dto.response.CommentListResponse
import kuke.board.comment.dto.response.CommentResponse
import kuke.board.comment.service.CommentService
import kuke.board.dto.response.CommonCursorResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/comments")
class CommentController(
    private val commentService: CommentService,
) {

    @PostMapping
    fun create(
        @RequestBody request: CommentCreateRequest
    ): CommentResponse {
        return commentService.create(request)
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
        @RequestBody request: CommentUpdateRequest
    ): CommentResponse {
        return commentService.update(commentId, request)
    }

    @DeleteMapping("/{commentId}")
    fun delete(
        @PathVariable commentId: Long
    ) {
        commentService.delete(commentId)
    }
}
