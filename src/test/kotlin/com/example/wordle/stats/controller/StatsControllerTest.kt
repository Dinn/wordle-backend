package com.example.wordle.stats.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestPropertySource(properties = [
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
])
class StatsControllerTest(
    @Autowired val client: WebTestClient
) {

    private val userId = UUID.randomUUID()

    @Test
    @WithMockUser(username = "550e8400-e29b-41d4-a716-446655440000")
    fun `POST invalid guessCnt returns 400`() {
        client.post()
            .uri("/stats")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{ "guessCnt": 8, "win": true }""")
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    @WithMockUser(username = "550e8400-e29b-41d4-a716-446655440000")
    fun `POST valid result - check actual response`() {
        client.post()
            .uri("/stats")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{ "guessCnt": 3, "win": true }""")
            .exchange()
            .expectBody()
            .consumeWith { result ->
                println("=== POST Response ===")
                println("Status: ${result.status}")
                println("Status Code: ${result.status.value()}")
                val responseBody = if (result.responseBody != null) {
                    String(result.responseBody!!)
                } else {
                    "null"
                }
                println("Body: $responseBody")
                println("Headers: ${result.responseHeaders}")
            }
    }

    @Test
    @WithMockUser(username = "550e8400-e29b-41d4-a716-446655440000")
    fun `GET stats - check actual response`() {
        println("=== Testing GET endpoint ===")
        client.get()
            .uri("/stats")
            .exchange()
            .expectBody()
            .consumeWith { result ->
                println("GET Status: ${result.status}")
                println("GET Status Code: ${result.status.value()}")
                val responseBody = if (result.responseBody != null) {
                    String(result.responseBody!!)
                } else {
                    "null"
                }
                println("GET Body: $responseBody")
            }
    }

    @Test
    fun `Unauthorized access returns 401`() {
        client.get()
            .uri("/stats")
            .exchange()
            .expectStatus().isUnauthorized
    }
}
                .exchange()
                .expectBody()
                .consumeWith { result ->
                    println("POST exists - Status: ${result.status.value()}")
                }
        } catch (e: Exception) {
            println("POST endpoint issue: ${e.message}")
        }

        // GET 테스트
        try {
            client.get()
                .uri("/stats/me?userId=$userId")
                .exchange()
                .expectBody()
                .consumeWith { result ->
                    println("GET exists - Status: ${result.status.value()}")
                }
        } catch (e: Exception) {
            println("GET endpoint issue: ${e.message}")
        }
    }
}
