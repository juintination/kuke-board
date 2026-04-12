package kuke.board.user.dto.response

import kuke.board.user.entity.User

data class UserResponse(
    val id: Long,
    val email: String,
    val nickname: String,
) {
    companion object {
        fun from(
            user: User,
        ) = UserResponse(
            id = user.id!!,
            email = user.email,
            nickname = user.nickname,
        )
    }
}
