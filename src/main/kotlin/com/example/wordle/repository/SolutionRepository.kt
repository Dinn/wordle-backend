package com.example.wordle.repository

import com.example.wordle.entity.SolutionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface SolutionRepository : JpaRepository<SolutionEntity, Long> {
    
    /**
     * 활성화된 모든 솔루션 조회
     */
    fun findByIsActiveTrue(): List<SolutionEntity>
    
    /**
     * 랜덤한 활성화된 솔루션 하나 조회
     */
    @Query("SELECT s FROM SolutionEntity s WHERE s.isActive = true ORDER BY RANDOM() LIMIT 1")
    fun findRandomActiveSolution(): SolutionEntity?
    
    /**
     * 특정 단어로 솔루션 조회
     */
    fun findByWordIgnoreCase(word: String): SolutionEntity?
    
    /**
     * 단어 존재 여부 확인
     */
    fun existsByWordIgnoreCase(word: String): Boolean
}
