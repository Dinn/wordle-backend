package com.example.wordle.stats.domain

import jakarta.persistence.Embeddable

/**
 * 시도(1~6회)별 정답 횟수 분포.
 *  - Value Object → 불변 data class
 *  - @Embeddable : PlayerStats 테이블의 컬럼으로 평탄화
 */
@Embeddable
data class GuessDist(
    val one:   Int = 0,
    val two:   Int = 0,
    val three: Int = 0,
    val four:  Int = 0,
    val five:  Int = 0,
    val six:   Int = 0
) {
    /** 총 승리 횟수 합계 계산 (필요 시) */
    fun totalWins(): Int = one + two + three + four + five + six
}
