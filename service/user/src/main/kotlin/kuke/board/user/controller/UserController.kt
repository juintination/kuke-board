package kuke.board.user.controller

import kuke.board.user.dto.request.LoginRequest
import kuke.board.user.dto.request.SignupRequest
import kuke.board.user.dto.response.TokenResponse
import kuke.board.user.dto.response.UserResponse
import kuke.board.user.service.UserService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
) {

    @PostMapping("/signup")
    fun signup(
        @RequestBody request: SignupRequest,
    ): TokenResponse {
        return userService.signup(request)
    }

    @PostMapping("/login")
    fun login(
        @RequestBody request: LoginRequest,
    ): TokenResponse {
        return userService.login(request)
    }

    @GetMapping("{userId}")
    fun get(
        @PathVariable userId: Long,
    ): UserResponse {
        return userService.get(userId)
    }
}
