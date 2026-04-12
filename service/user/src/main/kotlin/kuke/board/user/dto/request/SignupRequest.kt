package kuke.board.user.dto.request

data class SignupRequest(
    val email: String,
    val password: String,
    val nickname: String,
)
