package kuke.board.article

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(kuke.board.jpa.config.JpaAuditConfig::class)
class ArticleApplication

fun main(args: Array<String>) {
    runApplication<ArticleApplication>(*args)
}
