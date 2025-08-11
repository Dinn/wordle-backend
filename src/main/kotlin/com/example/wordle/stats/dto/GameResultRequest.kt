package com.example.wordle.stats.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

/**
 * 한 판이 끝났을 때 클라이언트가 보내는 JSON.
 *
 * {
 *   "guessCnt": 3,
 *   "win": true
 * }
 */
data class GameResultRequest(
    @field:NotNull(message = "추측 횟수는 필수입니다")
    @field:Min(value = 1, message = "추측 횟수는 1 이상이어야 합니다")
    @field:Max(value = 6, message = "추측 횟수는 6 이하여야 합니다")
    val guessCnt: Int?,

    @field:NotNull(message = "승패 결과는 필수입니다")
    val win: Boolean?
)
)
