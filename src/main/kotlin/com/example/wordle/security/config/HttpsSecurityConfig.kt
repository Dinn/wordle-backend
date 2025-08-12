package com.example.wordle.security.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter

/**
 * HTTPS 및 보안 헤더 설정
 */
@Configuration
@EnableWebSecurity
class HttpsSecurityConfig {

    /**
     * 프로덕션 환경에서 HTTPS 강제 및 보안 헤더 설정
     */
    @Bean
    @Profile("prod")
    fun httpsSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .requiresChannel { channel ->
                channel.anyRequest().requiresSecure()
            }
            .headers { headers ->
                headers
                    // HSTS (HTTP Strict Transport Security) 활성화
                    .httpStrictTransportSecurity { hsts ->
                        hsts.maxAgeInSeconds(31536000) // 1년
                    }
                    // Content Security Policy
                    .contentSecurityPolicy { csp ->
                        csp.policyDirectives("default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self'; connect-src 'self'; frame-ancestors 'none';")
                    }
                    
                    // X-Frame-Options: 클릭재킹 방지
                    .frameOptions { frameOptions ->
                        frameOptions.deny()
                    }
                    
                    // X-Content-Type-Options: MIME 타입 스니핑 방지
                    .contentTypeOptions { }
                    
                    // Referrer Policy: 레퍼러 정보 제한
                    .referrerPolicy { referrerPolicy ->
                        referrerPolicy.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                    }
            }
            .build()
    }

    /**
     * 개발 환경에서 기본 보안 헤더만 설정 (HTTPS 강제 제외)
     */
    @Bean
    @Profile("!prod")
    fun developmentSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .headers { headers ->
                headers
                    // Content Security Policy (개발용으로 완화)
                    .contentSecurityPolicy { csp ->
                        csp.policyDirectives("default-src 'self' 'unsafe-inline' 'unsafe-eval'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; style-src 'self' 'unsafe-inline';")
                    }
                    
                    // X-Frame-Options
                    .frameOptions { frameOptions ->
                        frameOptions.sameOrigin()
                    }
                    
                    // X-Content-Type-Options
                    .contentTypeOptions { }
                    
                    // Referrer Policy
                    .referrerPolicy { referrerPolicy ->
                        referrerPolicy.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                    }
            }
            .build()
    }
}
