package kuke.board.user.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.security.Key
import java.util.*

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret-key}")
    private val secretKey: String,

    @Value("\${jwt.access-expiration-ms}")
    private val accessExpirationMs: Long,
) {

    private lateinit var key: Key

    @PostConstruct
    fun init() {
        key = Keys.hmacShaKeyFor(secretKey.toByteArray(StandardCharsets.UTF_8))
    }

    fun createToken(
        userId: Long,
    ): String {
        val now = Date()
        val expiration = Date(now.time + accessExpirationMs)

        return Jwts.builder()
            .subject(userId.toString())
            .issuedAt(now)
            .expiration(expiration)
            .signWith(key)
            .compact()
    }
}
