package com.example.wordle.controller

import com.example.wordle.service.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/dict")
class DictionaryController(
    private val searchService: SearchService
) {
    @GetMapping("/meaning")
    fun meaning(
        @RequestParam q: String,
        @RequestParam(defaultValue = "true") onlyGeneral: Boolean
    ): Mono<ResponseEntity<MeaningResult>> {
        if (q.isBlank()) return Mono.just(ResponseEntity.badRequest().build())
        return searchService.fetchMeanings(q, onlyGeneral)
            .map { ResponseEntity.ok(it) }
    }

    @GetMapping("/meaning/detail")
    fun meaningDetail(@RequestParam q: String): Mono<ResponseEntity<ViewResult>> {
        if (q.isBlank()) return Mono.just(ResponseEntity.badRequest().build())
        return searchService.fetchWordInfoByWordAndSense(q)
            .map { ResponseEntity.ok(it) }
    }
}
