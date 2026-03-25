package kuke.board.article

import kuke.board.common.jpa.config.JpaAuditConfig
import kuke.board.common.outbox.config.MessageRelayConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EntityScan(
    "kuke.board.article.entity",
    "kuke.board.common.outbox.entity",
)
@EnableJpaRepositories(
    "kuke.board.article.repository",
    "kuke.board.common.outbox.repository",
)
@SpringBootApplication
@Import(
    JpaAuditConfig::class,
    MessageRelayConfig::class,
)
class ArticleApplication

fun main(args: Array<String>) {
    runApplication<ArticleApplication>(*args)
}
