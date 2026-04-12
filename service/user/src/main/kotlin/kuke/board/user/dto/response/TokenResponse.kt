package kuke.board.user.dto.response

data class TokenResponse(
    val accessToken: String,
) {
    companion object {
        fun of(
            accessToken: String,
        ): TokenResponse {
            return TokenResponse(
                accessToken = accessToken,
            )
        }
    }
}
