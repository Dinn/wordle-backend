package com.example.wordle.security.config

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * Rate Limiting 설정
 * OAuth2 엔드포인트에 대한 브루트포스 공격 방지
 */
@Configuration
@EnableCaching
class RateLimitingConfig {

    data class RateLimitEntry(
        val count: AtomicInteger = AtomicInteger(0),
        val windowStart: LocalDateTime = LocalDateTime.now()
    )

    private val tokenLimits = ConcurrentHashMap<String, RateLimitEntry>()
    private val loginLimits = ConcurrentHashMap<String, RateLimitEntry>()
    private val signupLimits = ConcurrentHashMap<String, RateLimitEntry>()

    /**
     * OAuth2 토큰 엔드포인트용 Rate Limiting
     * - 분당 최대 10회 요청 허용
     */
    fun checkTokenEndpointLimit(clientIp: String): Boolean {
        return checkRateLimit(tokenLimits, clientIp, 10, 1)
    }

    /**
     * 로그인 엔드포인트용 Rate Limiting  
     * - 분당 최대 5회 로그인 시도 허용
     */
    fun checkLoginEndpointLimit(clientIp: String): Boolean {
        return checkRateLimit(loginLimits, clientIp, 5, 1)
    }

    /**
     * 회원가입 엔드포인트용 Rate Limiting
     * - 시간당 최대 3회 회원가입 허용
     */
    fun checkSignupEndpointLimit(clientIp: String): Boolean {
        return checkRateLimit(signupLimits, clientIp, 3, 60)
    }

    /**
     * Rate Limit 체크 로직
     * @param limitMap 제한 맵
     * @param key 키 (보통 IP 주소)
     * @param maxRequests 최대 요청 수
     * @param windowMinutes 시간 윈도우 (분)
     */
    private fun checkRateLimit(
        limitMap: ConcurrentHashMap<String, RateLimitEntry>,
        key: String,
        maxRequests: Int,
        windowMinutes: Long
    ): Boolean {
        val now = LocalDateTime.now()
        val entry = limitMap.computeIfAbsent(key) { RateLimitEntry() }

        // 시간 윈도우가 지났으면 리셋
        if (entry.windowStart.plusMinutes(windowMinutes).isBefore(now)) {
            entry.count.set(0)
            limitMap[key] = entry.copy(windowStart = now)
        }

        // 현재 요청 수가 제한을 초과하는지 확인
        return entry.count.incrementAndGet() <= maxRequests
    }

    /**
     * 주기적으로 오래된 엔트리 정리 (메모리 누수 방지)
     */
    @Bean
    fun rateLimitCleaner(): Runnable {
        return Runnable {
            val cutoff = LocalDateTime.now().minusHours(1)
            listOf(tokenLimits, loginLimits, signupLimits).forEach { map ->
                map.entries.removeIf { it.value.windowStart.isBefore(cutoff) }
            }
        }
    }
}
