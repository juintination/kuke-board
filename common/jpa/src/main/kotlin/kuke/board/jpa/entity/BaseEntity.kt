package kuke.board.jpa.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity {

    @CreatedDate
    @Column(nullable = false, updatable = false, columnDefinition = "DATETIME(6)")
    var createdAt: LocalDateTime = LocalDateTime.now()
        protected set

    @LastModifiedDate
    @Column(nullable = false, columnDefinition = "DATETIME(6)")
    var modifiedAt: LocalDateTime = LocalDateTime.now()
        protected set

    @Column(columnDefinition = "DATETIME(6)")
    var deletedAt: LocalDateTime? = null
        protected set
}
