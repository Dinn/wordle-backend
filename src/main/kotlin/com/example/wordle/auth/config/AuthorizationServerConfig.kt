package com.example.wordle.auth.config

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.core.io.Resource
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.core.oidc.OidcScopes
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import java.io.InputStream
import java.security.KeyStore
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.time.Duration
import java.util.*

@Configuration
class AuthorizationServerConfig {

    @Value("\${spring.security.oauth2.authorizationserver.key-store}")
    private lateinit var keyStoreLocation: Resource

    @Value("\${spring.security.oauth2.authorizationserver.key-store-password}")
    private lateinit var keyStorePassword: String

    @Value("\${spring.security.oauth2.authorizationserver.key-alias}")
    private lateinit var keyAlias: String

    /** OAuth2 인증 서버 전용 시큐리티 필터 체인 */
    @Bean
    @Order(1)
    fun authorizationServerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http)
        http.getConfigurer(OAuth2AuthorizationServerConfigurer::class.java)
            .oidc(Customizer.withDefaults())
        
        return http
            .exceptionHandling { exceptions ->
                exceptions.defaultAuthenticationEntryPointFor(
                    LoginUrlAuthenticationEntryPoint("/login"),
                    { request -> request.requestURI.startsWith("/oauth2/") }
                )
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt(Customizer.withDefaults())
            }
            .build()
    }

    /** JDBC 기반 클라이언트 저장소 */
    @Bean
    fun registeredClientRepository(jdbcTemplate: JdbcTemplate): RegisteredClientRepository {
        val repository = JdbcRegisteredClientRepository(jdbcTemplate)
        
        val client = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("wordle-client")
            .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .redirectUri("http://localhost:3000/callback")
            .redirectUri("http://localhost:3000/silent-renew")
            .postLogoutRedirectUri("http://localhost:3000/")
            .scope(OidcScopes.OPENID)
            .scope(OidcScopes.PROFILE)
            .scope("read")
            .scope("write")
            .clientSettings(ClientSettings.builder()
                .requireAuthorizationConsent(false)
                .requireProofKey(true)
                .build())
            .tokenSettings(TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofMinutes(15))
                .refreshTokenTimeToLive(Duration.ofDays(7))
                .reuseRefreshTokens(false)
                .build())
            .build()

        repository.save(client)
        return repository
    }

    /** JKS 키스토어에서 RSA 키 로드 */
    @Bean
    fun jwkSource(): JWKSource<SecurityContext> {
        val keyStore = KeyStore.getInstance("PKCS12")
        val inputStream: InputStream = keyStoreLocation.inputStream
        keyStore.load(inputStream, keyStorePassword.toCharArray())
        
        val publicKey = keyStore.getCertificate(keyAlias).publicKey as RSAPublicKey
        val privateKey = keyStore.getKey(keyAlias, keyStorePassword.toCharArray()) as RSAPrivateKey
        
        val rsaKey = RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .keyID(keyAlias)
            .build()
            
        val jwkSet = JWKSet(rsaKey)
        return ImmutableJWKSet(jwkSet)
    }

    /** JWT 디코더 */
    @Bean
    fun jwtDecoder(jwkSource: JWKSource<SecurityContext>): JwtDecoder {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource)
    }

    /** 인증 서버 설정 - 운영 환경 issuer */
    @Bean
    fun authorizationServerSettings(): AuthorizationServerSettings {
        return AuthorizationServerSettings.builder()
            .issuer("https://auth.example.com")
            .build()
    }
}
