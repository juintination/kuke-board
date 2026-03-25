package kuke.board.comment

import kuke.board.common.jpa.config.JpaAuditConfig
import kuke.board.common.outbox.config.MessageRelayConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EntityScan(
    "kuke.board.comment.entity",
    "kuke.board.common.outbox.entity",
)
@EnableJpaRepositories(
    "kuke.board.comment.repository",
    "kuke.board.common.outbox.repository",
)
@SpringBootApplication
@Import(
    JpaAuditConfig::class,
    MessageRelayConfig::class,
)
class CommentApplication

fun main(args: Array<String>) {
    runApplication<CommentApplication>(*args)
}
