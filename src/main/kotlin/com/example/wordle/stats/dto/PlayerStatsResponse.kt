package com.example.wordle.stats.dto

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
        fun from(stats: Any): PlayerStatsResponse {
            return when (stats) {
                is Map<*, *> -> PlayerStatsResponse(
                    gamesPlayed = stats["gamesPlayed"] as? Int ?: 0,
                    gamesWon = stats["gamesWon"] as? Int ?: 0,
                    currentStreak = stats["currentStreak"] as? Int ?: 0,
                    maxStreak = stats["maxStreak"] as? Int ?: 0
                )
                else -> PlayerStatsResponse()
            }
        }
    }
}
    }
}
