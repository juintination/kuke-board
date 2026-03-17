package kuke.board.article.entity

import io.hypersistence.utils.hibernate.id.Tsid
import jakarta.persistence.*
import kuke.board.article.dto.request.ArticleCreateRequest
import kuke.board.article.dto.request.ArticleUpdateRequest
import kuke.board.common.jpa.entity.BaseEntity
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@Entity
@Table(
    name = "article",
    indexes = [
        Index(
            name = "idx_article_board_id_deleted_at_id",
            columnList = "board_id, deleted_at, id"
        )
    ]
)
@SQLRestriction("deleted_at is null")
@SQLDelete(sql = "update article set deleted_at = now() where id = ?")
class Article private constructor(

    @Id
    @Tsid
    @Column(columnDefinition = "BIGINT UNSIGNED")
    val id: Long? = null,

    @Column(length = 100, nullable = false)
    var title: String,

    @Column(length = 3000, nullable = false)
    var content: String,

    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    val boardId: Long,

    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    val writerId: Long,
) : BaseEntity() {
    companion object {
        fun create(
            request: ArticleCreateRequest,
        ) = Article(
            title = request.title,
            content = request.content,
            boardId = request.boardId,
            writerId = request.writerId,
        )
    }

    fun update(
        request: ArticleUpdateRequest,
    ) {
        this.title = request.title
        this.content = request.content
    }
}
