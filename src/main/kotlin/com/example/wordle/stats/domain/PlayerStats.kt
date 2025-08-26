package com.example.wordle.stats.domain

import jakarta.persistence.*
import java.util.*

/**
 * 사용자별 누적 통계 엔티티.
 *  - PK = userId(UUID) : User 테이블과 1:1 예상
 *  - GuessDist를 @Embedded 로 포함
 */
@Entity
@Table(name = "player_stats")
data class PlayerStats(

    @Id
    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    var games: Int = 0,
    var wins: Int = 0,

    @Column(name = "cur_streak")
    var curStreak: Int = 0,

    @Column(name = "max_streak")
    var maxStreak: Int = 0,

    @Embedded
    var dist: GuessDist = GuessDist()
) {
    
    // JPA를 위한 기본 생성자
    constructor() : this(UUID.randomUUID())

    /** 편의 계산 속성 – DB 컬럼으로 두지 않음 */
    val winRate: Double
        get() = if (games == 0) 0.0 else wins.toDouble() / games
}
