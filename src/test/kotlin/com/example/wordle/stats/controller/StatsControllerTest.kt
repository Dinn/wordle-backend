package com.example.wordle.stats.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = [
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.security.oauth2.authorizationserver.key-store=classpath:test-keystore.p12",
    "spring.security.oauth2.authorizationserver.key-store-password=changeit",
    "spring.security.oauth2.authorizationserver.key-alias=oauth-key"
])
class StatsControllerTest(
    @Autowired val mockMvc: MockMvc
) {

    @Test
    @WithMockUser(username = "550e8400-e29b-41d4-a716-446655440000")
    fun `GET stats returns user statistics`() {
        mockMvc.perform(get("/stats"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    }

    @Test
    fun `Unauthorized access returns 401`() {
        mockMvc.perform(get("/stats"))
            .andExpect(status().isUnauthorized)
    }
}
