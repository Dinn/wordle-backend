package com.example.wordle.stats.service

import com.example.wordle.stats.domain.PlayerStats
import com.example.wordle.stats.repository.StatsRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class StatsService(
    private val repo: StatsRepository
) {
    
    /**
     * 게임 결과 기록
     */
    @Transactional
    fun recordGame(userId: UUID, guessCnt: Int, win: Boolean) {
        // 1) 기존 통계 조회 또는 새로 생성
        val stats = repo.findById(userId).orElse(PlayerStats(userId))
        
        // 2) 게임 수 증가
        stats.games++
        
        if (win) {
            // 승리 시
            stats.wins++
            stats.curStreak++
            if (stats.curStreak > stats.maxStreak) {
                stats.maxStreak = stats.curStreak
            }
            
            // 3) GuessDist 값 객체는 copy 로 불변성 유지
            stats.dist = when (guessCnt) {
                1 -> stats.dist.copy(one   = stats.dist.one   + 1)
                2 -> stats.dist.copy(two   = stats.dist.two   + 1)
                3 -> stats.dist.copy(three = stats.dist.three + 1)
                4 -> stats.dist.copy(four  = stats.dist.four  + 1)
                5 -> stats.dist.copy(five  = stats.dist.five  + 1)
                else -> stats.dist.copy(six = stats.dist.six + 1)
            }
        } else {
            // 패배 시 연승 초기화
            stats.curStreak = 0
        }

        repo.save(stats)
    }

    /**
     * 현재 통계 조회.
     * 없으면 기본값(0)으로 생성해 반환.
     */
    @Transactional(readOnly = true)
    fun getStats(userId: UUID): PlayerStats =
        repo.findById(userId).orElse(PlayerStats(userId))
}
