package kuke.board.comment

import kuke.board.jpa.config.JpaAuditConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(JpaAuditConfig::class)
class CommentApplication

fun main(args: Array<String>) {
    runApplication<CommentApplication>(*args)
}
