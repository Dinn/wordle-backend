package com.example.wordle.stats.service

import org.springframework.stereotype.Service
import java.util.*

@Service
class StatsService {
    
    fun getStats(userId: UUID): Map<String, Any> {
        // 임시 구현 - 실제 DB 조회 로직 추가 필요
        return mapOf(
            "gamesPlayed" to 0,
            "gamesWon" to 0,
            "currentStreak" to 0,
            "maxStreak" to 0,
            "guessDistribution" to mapOf(
                1 to 0,
                2 to 0,
                3 to 0,
                4 to 0,
                5 to 0,
                6 to 0
            )
        )
    }
    
    fun recordGame(userId: UUID, guessCnt: Int, win: Boolean) {
        // 임시 구현 - 실제 게임 결과 저장 로직 추가 필요
        println("Recording game for user $userId: guesses=$guessCnt, win=$win")
        
        // 여기에 실제 DB 저장 로직이 들어갈 예정
        // 예: gameStatsRepository.save(GameStats(userId, guessCnt, win))
    }
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

        repo.save(stats)          // INSERT -or- UPDATE
    }

    /**
     * 현재 통계 조회.
     * 없으면 기본값(0)으로 생성해 반환.
     */
    @Transactional(readOnly = true)
    fun getStats(userId: UUID): PlayerStats =
        repo.findById(userId).orElse(PlayerStats(userId))
}
