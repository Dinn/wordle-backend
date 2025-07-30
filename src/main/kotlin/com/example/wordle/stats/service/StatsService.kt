package com.example.wordle.stats.service

import com.example.wordle.stats.domain.GuessDist
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
     * 게임 종료 시 호출.
     *
     * @param userId     사용자 식별자
     * @param guessCnt   1‥6  ― 정답까지 걸린 횟수
     * @param win        승리 여부 (true = 정답, false = 실패)
     */
    @Transactional
    fun recordGame(userId: UUID, guessCnt: Int, win: Boolean) {
        // 1) 동시성 대비: SELECT … FOR UPDATE
        val stats = repo.findWithLock(userId) ?: PlayerStats(userId)

        // 2) 공통 누적
        stats.games += 1

        if (win) {
            stats.wins += 1
            stats.curStreak += 1
            if (stats.curStreak > stats.maxStreak) stats.maxStreak = stats.curStreak

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
