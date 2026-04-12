package kuke.board.articleread.client

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class UserClient(
    @Value("\${endpoints.kuke-board-user-service.url}")
    private val userServiceUrl: String,
) {

    private val log = KotlinLogging.logger {}

    private val restClient: RestClient by lazy {
        RestClient.create(userServiceUrl)
    }

    fun read(
        userId: Long,
    ): UserResponse? {
        return try {
            restClient.get()
                .uri("/api/users/{userId}", userId)
                .retrieve()
                .body(UserResponse::class.java)
        } catch (
            e: Exception,
        ) {
            log.error(e) { "[UserClient.read] userId=$userId" }
            null
        }
    }

    data class UserResponse(
        val id: Long,
        val nickname: String,
    )
}
