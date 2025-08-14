package com.example.wordle.auth.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository

/**
 * OAuth2 서비스 설정
 * JPA 엔티티 대신 Spring Authorization Server 공식 JDBC Repository 사용
 */
@Configuration
class OAuth2ServiceConfig {

    /**
     * OAuth2 Authorization Service (JDBC 기반)
     * - SAS 공식 구현체 사용으로 버전 호환성 보장
     * - 내부 직렬화 포맷 의존성 제거
     */
    @Bean
    @ConditionalOnProperty(
        prefix = "spring.security.oauth2.authorizationserver", 
        name = ["enabled"], 
        havingValue = "true", 
        matchIfMissing = true
    )
    fun authorizationService(
        jdbcTemplate: JdbcTemplate,
        registeredClientRepository: RegisteredClientRepository
    ): OAuth2AuthorizationService {
        return JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository)
    }

    /**
     * OAuth2 Authorization Consent Service (JDBC 기반)
     * - 동의 관리를 위한 공식 JDBC 구현체
     */
    @Bean
    @ConditionalOnProperty(
        prefix = "spring.security.oauth2.authorizationserver", 
        name = ["enabled"], 
        havingValue = "true", 
        matchIfMissing = true
    )
    fun authorizationConsentService(
        jdbcTemplate: JdbcTemplate,
        registeredClientRepository: RegisteredClientRepository
    ): OAuth2AuthorizationConsentService {
        return JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository)
    }
}
