package kuke.board.article.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "article")
class Article private constructor(

    @Id
    @Column(columnDefinition = "BIGINT UNSIGNED")
    val articleId: Long,

    @Column(length = 100, nullable = false)
    var title: String,

    @Column(length = 3000, nullable = false)
    var content: String,

    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    val boardId: Long,

    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    val writerId: Long,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var modifiedAt: LocalDateTime = createdAt
) {
    protected constructor() : this(
        articleId = 0,
        title = "",
        content = "",
        boardId = 0,
        writerId = 0,
        createdAt = LocalDateTime.now(),
        modifiedAt = LocalDateTime.now()
    )

    companion object {
        fun create(
            articleId: Long,
            title: String,
            content: String,
            boardId: Long,
            writerId: Long
        ): Article {
            require(title.length <= 100) {
                "Title cannot exceed 100 characters."
            }

            require(content.length <= 3000) {
                "Content cannot exceed 3000 characters."
            }

            return Article(
                articleId = articleId,
                title = title,
                content = content,
                boardId = boardId,
                writerId = writerId
            )
        }
    }

    fun update(title: String, content: String) {
        require(title.length <= 100) {
            "Title cannot exceed 100 characters."
        }

        require(content.length <= 3000) {
            "Content cannot exceed 3000 characters."
        }

        this.title = title
        this.content = content
        this.modifiedAt = LocalDateTime.now()
    }
}
