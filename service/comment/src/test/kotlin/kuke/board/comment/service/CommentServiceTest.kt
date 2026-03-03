package kuke.board.comment.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.*
import kuke.board.comment.entity.Comment
import kuke.board.comment.repository.CommentRepository
import org.springframework.data.repository.findByIdOrNull

class CommentServiceDeleteTest : BehaviorSpec({

    val commentRepository = mockk<CommentRepository>()

    val commentService = CommentService(
        commentRepository = commentRepository,
    )

    afterTest {
        clearAllMocks()
    }

    Given("delete") {

        When("삭제 대상 댓글에 자식(대댓글)이 있으면") {
            Then("tombstone 처리만 하고 삭제 처리는 하지 않는다") {
                val commentId = 10L
                val comment = mockk<Comment>()

                every { commentRepository.findByIdOrNull(commentId) } returns comment
                every { commentRepository.existsByParentId(commentId) } returns true
                every { comment.tombstone() } just Runs

                commentService.delete(commentId)

                verifySequence {
                    commentRepository.findByIdOrNull(commentId)
                    commentRepository.existsByParentId(commentId)
                    comment.tombstone()
                }
                verify(exactly = 0) {
                    commentRepository.delete(any<Comment>())
                }
            }
        }

        When("삭제 대상 댓글에 자식이 없고, root 댓글이면") {
            Then("해당 댓글을 삭제 처리하고 종료한다") {
                val commentId = 10L
                val comment = mockk<Comment>()

                every { commentRepository.findByIdOrNull(commentId) } returns comment
                every { commentRepository.existsByParentId(commentId) } returns false
                every { commentRepository.delete(comment) } just Runs
                every { comment.parentId } returns null

                commentService.delete(commentId)

                verifySequence {
                    commentRepository.findByIdOrNull(commentId)
                    commentRepository.existsByParentId(commentId)
                    commentRepository.delete(comment)
                }
                verify(exactly = 0) {
                    comment.tombstone()
                }
            }
        }

        When("삭제 대상 댓글에 자식이 없고, parent가 tombstone이 아니면") {
            Then("child만 삭제 처리하고 parent는 정리하지 않는다") {
                val commentId = 10L
                val parentId = 9L

                val child = mockk<Comment>()
                val parent = mockk<Comment>()

                every { commentRepository.findByIdOrNull(commentId) } returns child
                every { commentRepository.existsByParentId(commentId) } returns false
                every { commentRepository.delete(child) } just Runs

                every { child.parentId } returns parentId
                every { commentRepository.findByIdOrNull(parentId) } returns parent
                every { parent.isTombstoned() } returns false

                commentService.delete(commentId)

                verifySequence {
                    commentRepository.findByIdOrNull(commentId)
                    commentRepository.existsByParentId(commentId)
                    commentRepository.delete(child)
                    commentRepository.findByIdOrNull(parentId)
                    parent.isTombstoned()
                }
                verify(exactly = 0) {
                    commentRepository.delete(parent)
                }
            }
        }

        When("삭제 대상 댓글에 자식이 없고, parent가 tombstone이지만 자식이 남아있으면") {
            Then("child만 삭제 처리하고 parent는 정리하지 않는다") {
                val commentId = 10L
                val parentId = 9L

                val child = mockk<Comment>()
                val parent = mockk<Comment>()

                every { commentRepository.findByIdOrNull(commentId) } returns child
                every { commentRepository.existsByParentId(commentId) } returns false
                every { commentRepository.delete(child) } just Runs

                every { child.parentId } returns parentId
                every { commentRepository.findByIdOrNull(parentId) } returns parent
                every { parent.isTombstoned() } returns true
                every { parent.id } returns parentId
                every { commentRepository.existsByParentId(parentId) } returns true

                commentService.delete(commentId)

                verifySequence {
                    commentRepository.findByIdOrNull(commentId)
                    commentRepository.existsByParentId(commentId)
                    commentRepository.delete(child)
                    commentRepository.findByIdOrNull(parentId)
                    parent.isTombstoned()
                    parent.id
                    commentRepository.existsByParentId(parentId)
                }
                verify(exactly = 0) {
                    commentRepository.delete(parent)
                }
            }
        }

        When("삭제 대상 댓글에 자식이 없고, parent가 tombstone이며 더 이상 자식이 없으면") {
            Then("child 삭제 처리 후 parent도 삭제 처리한다") {
                val commentId = 10L
                val parentId = 9L

                val child = mockk<Comment>()
                val parent = mockk<Comment>()

                every { commentRepository.findByIdOrNull(commentId) } returns child
                every { commentRepository.existsByParentId(commentId) } returns false
                every { commentRepository.delete(child) } just Runs

                every { child.parentId } returns parentId

                every { commentRepository.findByIdOrNull(parentId) } returns parent
                every { parent.isTombstoned() } returns true
                every { parent.id } returns parentId
                every { commentRepository.existsByParentId(parentId) } returns false
                every { commentRepository.delete(parent) } just Runs

                // 재귀 종료 조건: parent가 root
                every { parent.parentId } returns null

                commentService.delete(commentId)

                verifySequence {
                    commentRepository.findByIdOrNull(commentId)
                    commentRepository.existsByParentId(commentId)
                    commentRepository.delete(child)

                    commentRepository.findByIdOrNull(parentId)
                    parent.isTombstoned()
                    parent.id
                    commentRepository.existsByParentId(parentId)
                    commentRepository.delete(parent)

                    parent.parentId
                }
            }
        }

        When("삭제 대상 댓글이 존재하지 않으면") {
            Then("예외를 던진다") {
                val commentId = 10L
                every { commentRepository.findByIdOrNull(commentId) } returns null

                shouldThrow<IllegalArgumentException> {
                    commentService.delete(commentId)
                }

                verify(exactly = 0) {
                    commentRepository.delete(any<Comment>())
                }
            }
        }
    }
})
