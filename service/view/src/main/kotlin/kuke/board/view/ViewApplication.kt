package kuke.board.view

import kuke.board.common.outbox.config.MessageRelayConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EntityScan(
    "kuke.board.view.entity",
    "kuke.board.common.outbox.entity",
)
@EnableJpaRepositories(
    "kuke.board.view.repository",
    "kuke.board.common.outbox.repository",
)
@SpringBootApplication
@Import(
    MessageRelayConfig::class,
)
class ViewApplication

fun main(args: Array<String>) {
    runApplication<ViewApplication>(*args)
}
