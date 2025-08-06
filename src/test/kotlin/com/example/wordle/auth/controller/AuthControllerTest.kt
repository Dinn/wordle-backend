package com.example.wordle.auth.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestPropertySource(properties = [
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
])
class AuthControllerTest(
    @Autowired val client: WebTestClient
) {

    @Test
    fun `POST signup with valid data returns 201`() {
        val signupRequest = """
            {
                "username": "testuser",
                "email": "test@example.com",
                "password": "TestPass123!"
            }
        """.trimIndent()

        client.post()
            .uri("/auth/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(signupRequest)
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("$.username").isEqualTo("testuser")
            .jsonPath("$.email").isEqualTo("test@example.com")
            .jsonPath("$.message").isEqualTo("회원가입이 완료되었습니다")
    }

    @Test
    fun `POST signup with duplicate username returns 409`() {
        val signupRequest = """
            {
                "username": "duplicate",
                "email": "first@example.com",
                "password": "TestPass123!"
            }
        """.trimIndent()

        // 첫 번째 가입 성공
        client.post()
            .uri("/auth/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(signupRequest)
            .exchange()
            .expectStatus().isCreated

        // 두 번째 가입 실패 (중복 username)
        val duplicateRequest = """
            {
                "username": "duplicate",
                "email": "second@example.com",
                "password": "TestPass123!"
            }
        """.trimIndent()

        client.post()
            .uri("/auth/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(duplicateRequest)
            .exchange()
            .expectStatus().isEqualTo(409)
            .expectBody()
            .jsonPath("$.error").isEqualTo("Duplicate Resource")
            .jsonPath("$.message").isEqualTo("이미 존재하는 사용자명입니다")
    }

    @Test
    fun `POST signup with duplicate email returns 409`() {
        val signupRequest = """
            {
                "username": "user1",
                "email": "duplicate@example.com",
                "password": "TestPass123!"
            }
        """.trimIndent()

        // 첫 번째 가입 성공
        client.post()
            .uri("/auth/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(signupRequest)
            .exchange()
            .expectStatus().isCreated

        // 두 번째 가입 실패 (중복 email)
        val duplicateRequest = """
            {
                "username": "user2",
                "email": "duplicate@example.com",
                "password": "TestPass123!"
            }
        """.trimIndent()

        client.post()
            .uri("/auth/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(duplicateRequest)
            .exchange()
            .expectStatus().isEqualTo(409)
            .expectBody()
            .jsonPath("$.error").isEqualTo("Duplicate Resource")
            .jsonPath("$.message").isEqualTo("이미 존재하는 이메일입니다")
    }

    @Test
    fun `POST signup with invalid password returns 400`() {
        val signupRequest = """
            {
                "username": "testuser",
                "email": "test@example.com",
                "password": "weak"
            }
        """.trimIndent()

        client.post()
            .uri("/auth/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(signupRequest)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.error").isEqualTo("Validation Failed")
            .jsonPath("$.validationErrors.password").exists()
    }

    @Test
    fun `POST signup with invalid email returns 400`() {
        val signupRequest = """
            {
                "username": "testuser",
                "email": "invalid-email",
                "password": "TestPass123!"
            }
        """.trimIndent()

        client.post()
            .uri("/auth/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(signupRequest)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.error").isEqualTo("Validation Failed")
            .jsonPath("$.validationErrors.email").exists()
    }

    @Test
    fun `POST signup with empty username returns 400`() {
        val signupRequest = """
            {
                "username": "",
                "email": "test@example.com",
                "password": "TestPass123!"
            }
        """.trimIndent()

        client.post()
            .uri("/auth/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(signupRequest)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.error").isEqualTo("Validation Failed")
            .jsonPath("$.validationErrors.username").exists()
    }
}
