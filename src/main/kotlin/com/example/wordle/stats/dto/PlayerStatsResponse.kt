package com.example.wordle.stats.dto

import com.example.wordle.stats.domain.PlayerStats

data class PlayerStatsResponse(
    val gamesPlayed: Int = 0,
    val gamesWon: Int = 0,
    val currentStreak: Int = 0,
    val maxStreak: Int = 0,
    val guessDistribution: Map<Int, Int> = mapOf(
        1 to 0,
        2 to 0,
        3 to 0,
        4 to 0,
        5 to 0,
        6 to 0
    )
) {
    companion object {
        fun from(stats: PlayerStats): PlayerStatsResponse {
            return PlayerStatsResponse(
                gamesPlayed = stats.games,
                gamesWon = stats.wins,
                currentStreak = stats.curStreak,
                maxStreak = stats.maxStreak,
                guessDistribution = mapOf(
                    1 to stats.dist.one,
                    2 to stats.dist.two,
                    3 to stats.dist.three,
                    4 to stats.dist.four,
                    5 to stats.dist.five,
                    6 to stats.dist.six
                )
            )
        }
    }
}
