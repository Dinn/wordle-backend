package com.example.wordle.stats.controller

import com.example.wordle.stats.dto.GameResultRequest
import com.example.wordle.stats.dto.PlayerStatsResponse
import com.example.wordle.stats.service.StatsService
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/stats")
class StatsController(
    private val service: StatsService
) {
    /**
     * 현재 사용자 통계 조회
     * GET /stats
     */
    @GetMapping
    fun getMyStats(@AuthenticationPrincipal userDetails: UserDetails): PlayerStatsResponse {
        val userId = UUID.fromString(userDetails.username)
        return PlayerStatsResponse.from(service.getStats(userId))
    }

    /**
     * 게임 결과 기록
     * POST /stats
     */
    @PostMapping
    fun recordGameResult(
        @AuthenticationPrincipal userDetails: UserDetails,
        @Valid @RequestBody body: GameResultRequest
    ) {
        val userId = UUID.fromString(userDetails.username)
        service.recordGame(
            userId   = userId,
            guessCnt = body.guessCnt!!,
            win      = body.win!!
        )
    }
}
