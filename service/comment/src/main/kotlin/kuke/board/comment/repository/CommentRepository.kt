package kuke.board.comment.repository

import kuke.board.comment.entity.Comment
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository : JpaRepository<Comment, Long> {
}
