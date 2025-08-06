package com.example.wordle.stats.dto

import com.example.wordle.stats.domain.GuessDist
import com.example.wordle.stats.domain.PlayerStats
import java.util.*

data class PlayerStatsResponse(
    val userId:      UUID,
    val games:       Int,
    val wins:        Int,
    val winRate:     Double,
    val curStreak:   Int,
    val maxStreak:   Int,
    val dist:        GuessDist
) {
    companion object {
        fun from(entity: PlayerStats) = PlayerStatsResponse(
            userId     = entity.userId,
            games      = entity.games,
            wins       = entity.wins,
            winRate    = entity.winRate,
            curStreak  = entity.curStreak,
            maxStreak  = entity.maxStreak,
            dist       = entity.dist
        )
    }
}
