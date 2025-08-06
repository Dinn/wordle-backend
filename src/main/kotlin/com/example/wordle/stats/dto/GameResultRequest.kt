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

    @field:NotNull
    @field:Min(1) @field:Max(6)
    val guessCnt: Int?,

    @field:NotNull
    val win: Boolean?
)
