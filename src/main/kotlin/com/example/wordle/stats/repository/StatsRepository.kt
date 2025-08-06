package com.example.wordle.stats.repository

import com.example.wordle.stats.domain.PlayerStats
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.*
import jakarta.persistence.LockModeType
import org.springframework.transaction.annotation.Transactional

/**
 * - JpaRepository → CRUD + 페이징/정렬 기본 제공
 * - select for update 락, 누적 갱신용 커스텀 쿼리 샘플 포함
 */
interface StatsRepository : JpaRepository<PlayerStats, UUID> {

    /** 동시 업데이트 충돌 방지용 PESSIMISTIC_WRITE 예시 */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from PlayerStats s where s.userId = :userId")
    fun findWithLock(userId: UUID): PlayerStats?

    /** 1줄짜리 집계 UPDATE 예시(성능 최적) */
    @Modifying
    @Transactional
    @Query(
        """
        update PlayerStats s
           set s.games = s.games + 1,
               s.wins  = s.wins  + :win,
               s.curStreak = case when :win = 1 then s.curStreak + 1 else 0 end,
               s.maxStreak = greatest(s.maxStreak,
                                       case when :win = 1 then s.curStreak + 1 else 0 end)
         where s.userId = :userId
        """
    )
    fun accumulate(userId: UUID, win: Int): Int
}
