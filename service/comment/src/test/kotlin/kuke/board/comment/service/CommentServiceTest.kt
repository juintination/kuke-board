package kuke.board.comment.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.*
import kuke.board.comment.entity.Comment
import kuke.board.comment.repository.CommentRepository
import kuke.board.common.snowflake.Snowflake
import org.springframework.data.repository.findByIdOrNull

class CommentServiceTest : BehaviorSpec({

    val snowflake = Snowflake()
    val commentRepository = mockk<CommentRepository>()

    val commentService = CommentService(
        commentRepository = commentRepository,
    )

    fun commentFixture(
        id: Long = snowflake.nextId(),
        parentId: Long? = null,
        tombstoned: Boolean = false,
    ): Comment {
        val comment = mockk<Comment>()

        every { comment.id } returns id
        every { comment.parentId } returns parentId
        every { comment.isTombstoned() } returns tombstoned
        every { comment.tombstone() } just Runs

        return comment
    }

    afterTest {
        clearAllMocks()
    }

    Given("delete") {

        When("삭제 대상 댓글에 자식(대댓글)이 있으면") {
            Then("tombstone 처리만 하고 삭제 처리는 하지 않는다") {
                val commentId = 10L
                val comment = commentFixture(
                    id = commentId
                )

                every { commentRepository.findByIdOrNull(commentId) } returns comment
                every { commentRepository.existsByParentId(commentId) } returns true

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
                val comment = commentFixture(
                    id = commentId,
                    parentId = null
                )

                every { commentRepository.findByIdOrNull(commentId) } returns comment
                every { commentRepository.existsByParentId(commentId) } returns false
                every { commentRepository.delete(comment) } just Runs

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

                val child = commentFixture(
                    id = commentId,
                    parentId = parentId
                )
                val parent = commentFixture(
                    id = parentId,
                    tombstoned = false
                )

                every { commentRepository.findByIdOrNull(commentId) } returns child
                every { commentRepository.existsByParentId(commentId) } returns false
                every { commentRepository.delete(child) } just Runs

                every { commentRepository.findByIdOrNull(parentId) } returns parent

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

                val child = commentFixture(
                    id = commentId,
                    parentId = parentId
                )
                val parent = commentFixture(
                    id = parentId,
                    tombstoned = true
                )

                every { commentRepository.findByIdOrNull(commentId) } returns child
                every { commentRepository.existsByParentId(commentId) } returns false
                every { commentRepository.delete(child) } just Runs

                every { commentRepository.findByIdOrNull(parentId) } returns parent
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

                val child = commentFixture(
                    id = commentId,
                    parentId = parentId
                )
                val parent = commentFixture(
                    id = parentId,
                    parentId = null,
                    tombstoned = true
                )

                every { commentRepository.findByIdOrNull(commentId) } returns child
                every { commentRepository.existsByParentId(commentId) } returns false
                every { commentRepository.delete(child) } just Runs

                every { commentRepository.findByIdOrNull(parentId) } returns parent
                every { commentRepository.existsByParentId(parentId) } returns false
                every { commentRepository.delete(parent) } just Runs

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
