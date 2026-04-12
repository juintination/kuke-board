package kuke.board.gateway.filter

import kuke.board.gateway.jwt.JwtTokenProvider
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
) : AbstractGatewayFilterFactory<Any>(Any::class.java) {

    override fun apply(
        config: Any,
    ): GatewayFilter {
        return GatewayFilter { exchange, chain ->

            val authHeader = exchange.request.headers.getFirst("Authorization")

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return@GatewayFilter unauthorized(exchange)
            }

            val token = authHeader.substring(7)

            try {
                val userId = jwtTokenProvider.getUserId(token)

                val mutatedRequest = exchange.request.mutate()
                    .header("X-User-Id", userId.toString())
                    .build()

                return@GatewayFilter chain.filter(
                    exchange.mutate().request(mutatedRequest).build()
                )
            } catch (
                e: Exception,
            ) {
                return@GatewayFilter unauthorized(exchange)
            }
        }
    }

    private fun unauthorized(
        exchange: ServerWebExchange,
    ): Mono<Void> {
        exchange.response.statusCode = HttpStatus.UNAUTHORIZED
        return exchange.response.setComplete()
    }
}
