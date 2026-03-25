package kuke.board.hotarticle.controller

import kuke.board.hotarticle.dto.response.HotArticleResponse
import kuke.board.hotarticle.service.HotArticleService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/hot-articles")
class HotArticleController(
    private val hotArticleService: HotArticleService,
) {

    @GetMapping("/date/{date}")
    fun readAll(
        @PathVariable date: String,
    ): List<HotArticleResponse> {
        return hotArticleService.readAll(date)
    }
}
