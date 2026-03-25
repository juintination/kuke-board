package kuke.board.hotarticle.service

import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload
import kuke.board.hotarticle.repository.ArticleCreatedTimeRepository
import kuke.board.hotarticle.repository.HotArticleListRepository
import kuke.board.hotarticle.service.event.EventHandler
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDate

@Component
class HotArticleScoreUpdater(
    private val hotArticleListRepository: HotArticleListRepository,
    private val hotArticleScoreCalculator: HotArticleScoreCalculator,
    private val articleCreatedTimeRepository: ArticleCreatedTimeRepository,
) {

    fun update(
        event: Event<EventPayload>,
        eventHandler: EventHandler<EventPayload>,
    ) {
        val articleId = eventHandler.findArticleId(
            event = event,
        )

        val createdTime = articleCreatedTimeRepository.read(articleId)
            ?.takeIf { it.toLocalDate() == LocalDate.now() }
            ?: return

        eventHandler.handle(
            event = event,
        )

        val score = hotArticleScoreCalculator.calculate(
            articleId = articleId,
        )

        hotArticleListRepository.add(
            articleId = articleId,
            time = createdTime,
            score = score,
            limit = HOT_ARTICLE_COUNT,
            ttl = HOT_ARTICLE_TTL,
        )
    }

    companion object {
        private const val HOT_ARTICLE_COUNT = 10L
        private val HOT_ARTICLE_TTL: Duration = Duration.ofDays(10)
    }
}
