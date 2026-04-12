package kuke.board.user.service

import kuke.board.user.dto.request.LoginRequest
import kuke.board.user.dto.request.SignupRequest
import kuke.board.user.dto.response.TokenResponse
import kuke.board.user.entity.User
import kuke.board.user.jwt.JwtTokenProvider
import kuke.board.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder,
) {

    @Transactional
    fun signup(
        request: SignupRequest,
    ): TokenResponse {
        if (userRepository.findByEmail(request.email) != null) {
            throw IllegalStateException("이미 존재하는 이메일입니다.")
        }

        val passwordHash = passwordEncoder.encode(request.password)
            ?: throw IllegalArgumentException("비밀번호 해싱에 실패했습니다.")

        val user = userRepository.save(
            User.create(
                email = request.email,
                passwordHash = passwordHash,
                nickname = request.nickname,
            )
        )

        return TokenResponse.of(
            accessToken = jwtTokenProvider.createToken(
                userId = user.id!!,
            )
        )
    }

    @Transactional(readOnly = true)
    fun login(
        request: LoginRequest,
    ): TokenResponse {
        val user = userRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("존재하지 않는 이메일입니다.")

        if (!user.matchPassword(request.password, passwordEncoder)) {
            throw IllegalArgumentException("잘못된 비밀번호입니다.")
        }

        return TokenResponse.of(
            accessToken = jwtTokenProvider.createToken(
                userId = user.id!!,
            )
        )
    }
}
