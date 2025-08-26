package com.example.wordle.auth.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "oauth2_registered_client")
class OAuth2RegisteredClient(
    @Id
    @Column(length = 100)
    var id: String = "",
    
    @Column(name = "client_id", length = 100, nullable = false, unique = true)
    var clientId: String = "",
    
    @Column(name = "client_id_issued_at", nullable = false)
    var clientIdIssuedAt: Instant = Instant.now(),
    
    @Column(name = "client_secret", length = 200)
    var clientSecret: String? = null,
    
    @Column(name = "client_secret_expires_at")
    var clientSecretExpiresAt: Instant? = null,
    
    @Column(name = "client_name", length = 200, nullable = false)
    var clientName: String = "",
    
    @Column(name = "client_authentication_methods", length = 1000, nullable = false)
    var clientAuthenticationMethods: String = "",
    
    @Column(name = "authorization_grant_types", length = 1000, nullable = false)
    var authorizationGrantTypes: String = "",
    
    @Column(name = "redirect_uris", length = 1000)
    var redirectUris: String? = null,
    
    @Column(name = "post_logout_redirect_uris", length = 1000)
    var postLogoutRedirectUris: String? = null,
    
    @Column(name = "scopes", length = 1000, nullable = false)
    var scopes: String = "",
    
    @Column(name = "client_settings", length = 2000, nullable = false)
    var clientSettings: String = "",
    
    @Column(name = "token_settings", length = 2000, nullable = false)
    var tokenSettings: String = ""
)

@Entity
@Table(name = "oauth2_authorization")
class OAuth2Authorization(
    @Id
    @Column(length = 100)
    var id: String = "",
    
    @Column(name = "registered_client_id", length = 100, nullable = false)
    var registeredClientId: String = "",
    
    @Column(name = "principal_name", length = 200, nullable = false)
    var principalName: String = "",
    
    @Column(name = "authorization_grant_type", length = 100, nullable = false)
    var authorizationGrantType: String = "",
    
    @Column(name = "authorized_scopes", length = 1000)
    var authorizedScopes: String? = null,
    
    @Column(name = "attributes", columnDefinition = "TEXT")
    var attributes: String? = null,
    
    @Column(name = "state", length = 500)
    var state: String? = null,
    
    @Column(name = "authorization_code_value", columnDefinition = "TEXT")
    var authorizationCodeValue: String? = null,
    
    @Column(name = "authorization_code_issued_at")
    var authorizationCodeIssuedAt: Instant? = null,
    
    @Column(name = "authorization_code_expires_at")
    var authorizationCodeExpiresAt: Instant? = null,
    
    @Column(name = "authorization_code_metadata", columnDefinition = "TEXT")
    var authorizationCodeMetadata: String? = null,
    
    @Column(name = "access_token_value", columnDefinition = "TEXT")
    var accessTokenValue: String? = null,
    
    @Column(name = "access_token_issued_at")
    var accessTokenIssuedAt: Instant? = null,
    
    @Column(name = "access_token_expires_at")
    var accessTokenExpiresAt: Instant? = null,
    
    @Column(name = "access_token_metadata", columnDefinition = "TEXT")
    var accessTokenMetadata: String? = null,
    
    @Column(name = "access_token_type", length = 100)
    var accessTokenType: String? = null,
    
    @Column(name = "access_token_scopes", length = 1000)
    var accessTokenScopes: String? = null,
    
    @Column(name = "oidc_id_token_value", columnDefinition = "TEXT")
    var oidcIdTokenValue: String? = null,
    
    @Column(name = "oidc_id_token_issued_at")
    var oidcIdTokenIssuedAt: Instant? = null,
    
    @Column(name = "oidc_id_token_expires_at")
    var oidcIdTokenExpiresAt: Instant? = null,
    
    @Column(name = "oidc_id_token_metadata", columnDefinition = "TEXT")
    var oidcIdTokenMetadata: String? = null,
    
    @Column(name = "refresh_token_value", columnDefinition = "TEXT")
    var refreshTokenValue: String? = null,
    
    @Column(name = "refresh_token_issued_at")
    var refreshTokenIssuedAt: Instant? = null,
    
    @Column(name = "refresh_token_expires_at")
    var refreshTokenExpiresAt: Instant? = null,
    
    @Column(name = "refresh_token_metadata", columnDefinition = "TEXT")
    var refreshTokenMetadata: String? = null,
    
    @Column(name = "user_code_value", columnDefinition = "TEXT")
    var userCodeValue: String? = null,
    
    @Column(name = "user_code_issued_at")
    var userCodeIssuedAt: Instant? = null,
    
    @Column(name = "user_code_expires_at")
    var userCodeExpiresAt: Instant? = null,
    
    @Column(name = "user_code_metadata", columnDefinition = "TEXT")
    var userCodeMetadata: String? = null,
    
    @Column(name = "device_code_value", columnDefinition = "TEXT")
    var deviceCodeValue: String? = null,
    
    @Column(name = "device_code_issued_at")
    var deviceCodeIssuedAt: Instant? = null,
    
    @Column(name = "device_code_expires_at")
    var deviceCodeExpiresAt: Instant? = null,
    
    @Column(name = "device_code_metadata", columnDefinition = "TEXT")
    var deviceCodeMetadata: String? = null
)

@Entity
@Table(name = "oauth2_authorization_consent")
@IdClass(OAuth2AuthorizationConsentId::class)
class OAuth2AuthorizationConsent(
    @Id
    @Column(name = "registered_client_id", length = 100)
    var registeredClientId: String = "",
    
    @Id
    @Column(name = "principal_name", length = 200)
    var principalName: String = "",
    
    @Column(name = "authorities", length = 1000, nullable = false)
    var authorities: String = ""
)

data class OAuth2AuthorizationConsentId(
    var registeredClientId: String = "",
    var principalName: String = ""
) : java.io.Serializable
