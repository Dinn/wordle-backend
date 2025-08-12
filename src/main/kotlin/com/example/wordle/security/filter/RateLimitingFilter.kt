package com.example.wordle.security.filter

import com.example.wordle.security.config.RateLimitingConfig
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Rate Limiting 필터
 * OAuth2 및 인증 관련 엔드포인트에 대한 요청 제한
 */
@Component
class RateLimitingFilter(
    private val rateLimitingConfig: RateLimitingConfig
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(RateLimitingFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val clientIp = getClientIpAddress(request)
        val requestUri = request.requestURI
        val method = request.method

        // Rate Limiting 적용 대상 확인
        val isAllowed = when {
            // OAuth2 토큰 엔드포인트
            requestUri.startsWith("/oauth2/token") && method == "POST" -> 
                rateLimitingConfig.checkTokenEndpointLimit(clientIp)
            
            // 로그인 엔드포인트  
            requestUri.startsWith("/login") && method == "POST" ->
                rateLimitingConfig.checkLoginEndpointLimit(clientIp)
            
            // 회원가입 엔드포인트
            requestUri.startsWith("/auth/signup") && method == "POST" ->
                rateLimitingConfig.checkSignupEndpointLimit(clientIp)
            
            else -> true // Rate Limiting 대상이 아닌 경우 허용
        }

        if (isAllowed) {
            log.debug("Rate limit passed for IP: $clientIp, URI: $requestUri")
            filterChain.doFilter(request, response)
        } else {
            log.warn("Rate limit exceeded for IP: $clientIp, URI: $requestUri")
            response.status = HttpStatus.TOO_MANY_REQUESTS.value()
            response.contentType = "application/json"
            response.writer.write("""
                {
                    "status": 429,
                    "error": "Too Many Requests",
                    "message": "요청 한도를 초과했습니다. 잠시 후 다시 시도해주세요.",
                    "path": "$requestUri"
                }
            """.trimIndent())
            return
        }
    }

    /**
     * 클라이언트 IP 주소 추출 (프록시 환경 고려)
     */
    private fun getClientIpAddress(request: HttpServletRequest): String {
        val xForwardedFor = request.getHeader("X-Forwarded-For")
        val xRealIp = request.getHeader("X-Real-IP")
        
        return when {
            !xForwardedFor.isNullOrBlank() -> xForwardedFor.split(",")[0].trim()
            !xRealIp.isNullOrBlank() -> xRealIp
            else -> request.remoteAddr
        }
    }
}
