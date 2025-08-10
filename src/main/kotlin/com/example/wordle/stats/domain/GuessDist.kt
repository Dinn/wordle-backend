package com.example.wordle.stats.domain

import jakarta.persistence.Embeddable

/**
 * Wordle 정답 시도 분포 (1~6회).
 * - 값 타입(Value Object)이므로 불변(data class + copy 사용)
 * - @Embeddable: PlayerStats 엔티티에 내장되어 같은 테이블에 컬럼으로 펼쳐진다.
 */
@Embeddable
data class GuessDist(
    val one:  Int = 0,   // 1회 만에 맞춘 횟수
    val two:  Int = 0,   // 2회 …
    val three:Int = 0,
    val four: Int = 0,
    val five: Int = 0,
    val six:  Int = 0
) {
    /** 총 승리 횟수 합계 계산용 헬퍼 (필요하면 사용) */
    fun totalWins(): Int = one + two + three + four + five + six
}
