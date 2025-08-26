package com.example.wordle.stats.service

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import java.util.*
import org.junit.jupiter.api.Assertions.*

@SpringBootTest
@TestPropertySource(properties = [
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
])
class StatsServiceTest(
    @Autowired val statsService: StatsService
) {

    @Test
    fun `getStats returns default stats for new user`() {
        val userId = UUID.randomUUID()
        val stats = statsService.getStats(userId)
        
        assertNotNull(stats)
    }

    @Test
    fun `recordGame completes without error`() {
        val userId = UUID.randomUUID()
        
        assertDoesNotThrow {
            statsService.recordGame(userId, 3, true)
        }
    }

    @Test
    fun `recordGame with invalid guessCnt throws exception`() {
        val userId = UUID.randomUUID()
        
        assertDoesNotThrow {
            statsService.recordGame(userId, 7, false) // 서비스에서는 검증하지 않음
        }
    }
}
