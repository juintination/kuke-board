package kuke.board.hotarticle

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka

@EnableKafka
@SpringBootApplication
class HotArticleApplication

fun main(args: Array<String>) {
    runApplication<HotArticleApplication>(*args)
}
