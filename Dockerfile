# ──────────────────── 1) Build Stage ────────────────────
FROM gradle:8.5-jdk21 AS builder
WORKDIR /workspace

# Wrapper & 설정 먼저 복사 → 의존성 캐시 레이어
COPY gradlew ./
COPY gradle/wrapper/ gradle/wrapper/
COPY build.gradle.kts settings.gradle.kts ./
RUN chmod +x gradlew

# 소스·리소스 한 번에
COPY src ./src

# JAR 빌드 (테스트 제외)
RUN ./gradlew bootJar -x test --no-daemon

# ──────────────────── 2) Runtime Stage ────────────────────
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /workspace/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
