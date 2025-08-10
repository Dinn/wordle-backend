package com.example.wordle.stats.controller

import com.example.wordle.stats.dto.GameResultRequest
import com.example.wordle.stats.dto.PlayerStatsResponse
import com.example.wordle.stats.service.StatsService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/stats")
class StatsController(
    private val service: StatsService
) {
    /**
     * 내 통계 조회
     * GET /stats/me?userId=<uuid>
     */
    @GetMapping("/me")
    fun getMe(@RequestParam userId: UUID): PlayerStatsResponse =
        PlayerStatsResponse.from(service.getStats(userId))

    /**
     * 게임 결과 반영
     * POST /stats/update
     */
    @PostMapping("/update")
    fun record(
        @RequestParam userId: UUID,               // 실제 서비스에선 JWT 등에서 추출
        @Valid @RequestBody body: GameResultRequest
    ) {
        service.recordGame(
            userId   = userId,
            guessCnt = body.guessCnt!!,            // validation 통과 → non-null
            win      = body.win!!
        )
    }
}
