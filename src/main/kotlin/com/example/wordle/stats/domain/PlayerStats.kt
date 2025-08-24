package com.example.wordle.stats.domain

import jakarta.persistence.*
import java.util.*

/**
 * 사용자별 Wordle 통계 누적 엔티티.
 * - 테이블명: player_stats
 * - PK는 user_id(UUID) → 별도 User 테이블과 1:1 매핑 용이
 */
@Entity
@Table(name = "player_stats")
class PlayerStats(

    /** 사용자 식별자 = PK */
    @Id
    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    /** 총 게임 수 */
    var games: Int = 0,

    /** 총 승리 수 */
    var wins: Int = 0,

    /** 현재 연승 기록 */
    @Column(name = "cur_streak")
    var curStreak: Int = 0,

    /** 최고 연승 기록 */
    @Column(name = "max_streak")
    var maxStreak: Int = 0,

    /** 시도 횟수별 승리 분포 */
    @Embedded
    var dist: GuessDist = GuessDist()
) {

    /** 승률 편의 프로퍼티 (필요 시 DTO 변환 단계에서 사용) */
    val winRate: Double
        get() = if (games == 0) 0.0 else wins.toDouble() / games
}
