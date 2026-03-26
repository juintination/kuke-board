package kuke.board.articleread

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka

@EnableKafka
@SpringBootApplication
class ArticleReadApplication

fun main(args: Array<String>) {
    runApplication<ArticleReadApplication>(*args)
}
