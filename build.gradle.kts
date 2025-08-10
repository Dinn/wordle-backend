import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.spring") version "2.0.0"

    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.4"

    id("com.google.cloud.tools.jib") version "3.4.0"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
    id("io.gitlab.arturbosch.detekt")  version "1.23.6"
}

java.sourceCompatibility = JavaVersion.VERSION_21
repositories { mavenCentral() }

dependencies {
    /* ─ 앱 ─ */
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")  // 추가
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    
    /* ─ Database ─ */
    runtimeOnly("org.postgresql:postgresql")
    
    /* ─ OAuth 2.0 & JWT ─ */
    implementation("org.springframework.boot:spring-boot-starter-oauth2-authorization-server")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-oauth2-jose")

    /* ─ 테스트 ─ */
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.mockito", module = "mockito-core")
        exclude(group = "org.mockito", module = "mockito-junit-jupiter")
        exclude(group = "org.mockito", module = "mockito-inline")
    }
    testImplementation("com.h2database:h2")
    testImplementation("org.springframework.security:spring-security-test")
}

springBoot { mainClass.set("com.example.wordle.WordleApplicationKt") }

/* Kotlin 2.0 compilerOptions */
tasks.withType<KotlinCompile>().configureEach {
    compilerOptions { jvmTarget.set(JvmTarget.JVM_21) }
}

/* 테스트 JVM 설정 */
tasks.test {
    useJUnitPlatform()
    
    // JVM 모듈 시스템 관련 설정만 유지
    jvmArgs(
        "--add-opens", "java.base/java.lang=ALL-UNNAMED",
        "--add-opens", "java.base/java.util=ALL-UNNAMED"
    )
}

/* ─ jib, ktlint, detekt 등을 기존대로 유지 ─ */
jib {
    from.image = "eclipse-temurin:21-jre"
    to.image   = "registry.example.com/wordle-backend"
    to.tags    = setOf("latest", System.getenv("GITHUB_SHA") ?: "dev")
}

tasks.named("check") {
    dependsOn("ktlintCheck", "detekt")
}
