package kuke.board.like

import kuke.board.common.jpa.config.JpaAuditConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(JpaAuditConfig::class)
class LikeApplication

fun main(args: Array<String>) {
    runApplication<LikeApplication>(*args)
}
