package kuke.board.article

import kuke.board.jpa.config.JpaAuditConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(JpaAuditConfig::class)
class ArticleApplication

fun main(args: Array<String>) {
    runApplication<ArticleApplication>(*args)
}
