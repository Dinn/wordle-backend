# ───────────────────────────────────────────────
# 1) Build Stage – Gradle Wrapper로 JAR 생성
# ───────────────────────────────────────────────
FROM gradle:8.5-jdk21 AS builder
WORKDIR /workspace

# 1-A. 설정 파일 & Wrapper 먼저 복사  (의존성 레이어 캐시)
COPY gradlew gradle build.gradle.kts settings.gradle.kts ./
RUN chmod +x gradlew

# 1-B. 소스·리소스 복사
COPY src ./src
COPY src/main/resources ./src/main/resources

# 1-C. 빌드 (테스트는 일단 제외)  ⚡ 빠르고 실패 요인↓
RUN ./gradlew bootJar -x test --no-daemon

# ───────────────────────────────────────────────
# 2) Runtime Stage – 경량 JRE 이미지
# ───────────────────────────────────────────────
FROM eclipse-temurin:21-jre
WORKDIR /app

# 빌드 산출물만 복사  (최신 *.jar 하나)
COPY --from=builder /workspace/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
