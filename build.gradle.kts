import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // Kotlin
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.spring") version "2.0.0"

    // Spring Boot
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.4"

    // Quality & Build
    id("com.google.cloud.tools.jib") version "3.4.0"        // Dockerless 이미지
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"    // 포맷
    id("io.gitlab.arturbosch.detekt") version "1.23.6"      // 정적 분석
}

java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
}

dependencies {
    /* ───── 런타임 ───── */
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.boot:spring-boot-starter-actuator")


    /* ───── 테스트 ───── */
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk:1.13.10")
    testImplementation("org.testcontainers:junit-jupiter:1.19.8")
    testImplementation("org.testcontainers:postgresql:1.19.8")
    testImplementation("org.testcontainers:redis:1.19.8")
}

/* === Spring Boot JAR 설정 === */
springBoot {
    // Kotlin 메인 클래스는 컴파일 시 `Kt` 접미사가 붙습니다.
    mainClass.set("com.example.wordle.WordleApplicationKt")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "21"
}

tasks.test {
    useJUnitPlatform()
}

/* ───── Jib – CI에서 이미지 푸시용 (Dockerfile 불필요) ───── */
jib {
    from.image = "eclipse-temurin:21-jre"
    to.image   = "registry.example.com/wordle-backend"
    to.tags    = setOf("latest", System.getenv("GITHUB_SHA") ?: "dev")
}

/* ‘gradlew check’ → 포맷 + 정적분석 + 테스트 한 번에 */
tasks.named("check") {
    dependsOn("ktlintCheck", "detekt")
}
