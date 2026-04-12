package kuke.board.user.entity

import io.hypersistence.utils.hibernate.id.Tsid
import jakarta.persistence.*
import kuke.board.common.jpa.entity.BaseEntity
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import org.springframework.security.crypto.password.PasswordEncoder

@Entity
@Table(
    name = "users",
    indexes = [
        Index(
            name = "idx_user_email",
            columnList = "email"
        )
    ]
)
@SQLRestriction("deleted_at is null")
@SQLDelete(sql = "update user set deleted_at = now() where id = ?")
class User private constructor(

    @Id
    @Tsid
    @Column(columnDefinition = "BIGINT UNSIGNED")
    val id: Long? = null,

    @Column(length = 100, nullable = false, unique = true)
    val email: String,

    @Column(length = 255, nullable = false)
    private var passwordHash: String,

    @Column(length = 50, nullable = false)
    val nickname: String,
) : BaseEntity() {

    companion object {
        fun create(
            email: String,
            passwordHash: String,
            nickname: String,
        ): User {
            return User(
                email = email,
                passwordHash = passwordHash,
                nickname = nickname,
            )
        }
    }

    fun matchPassword(
        rawPassword: String,
        passwordEncoder: PasswordEncoder
    ): Boolean {
        return passwordEncoder.matches(rawPassword, passwordHash)
    }
}
