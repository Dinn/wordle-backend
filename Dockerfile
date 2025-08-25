#######################################################################
# Wordle-backend ‖ Multi-Arch, License-Free Dockerfile
# ----------------------------------------------------
# • Build  : Gradle 8.5 + Temurin JDK 21   (Apache 2.0 / EPL 2.0)
# • Runtime: Temurin JDK 21               (EPL 2.0 OR GPLv2+CE)
# • Works   on linux/amd64 (Intel/AMD) and linux/arm64 (Apple Silicon)
#######################################################################

############### 1️⃣ Build Stage #######################################
FROM --platform=${BUILDPLATFORM} gradle:8.5-jdk21 AS build

WORKDIR /app

# Copy everything (Docker-layer cache hits on unchanged files)
COPY . .

# Produce an executable JAR (tests 생략 → 빨리 빌드)
RUN ./gradlew clean bootJar -x test

############### 2️⃣ Runtime Stage #####################################
FROM --platform=${TARGETPLATFORM} eclipse-temurin:21-jdk

# 명시적 라이선스 라벨(SBOM · 리뷰 용이)
LABEL org.opencontainers.image.licenses="EPL-2.0 OR GPL-2.0-only WITH Classpath-exception-2.0"

# PostgreSQL 클라이언트 설치 추가
RUN apt-get update && apt-get install -y postgresql-client && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# 빌드한 JAR 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 애플리케이션 포트
EXPOSE 8080

# 컨테이너 시작 시 실행할 명령
ENTRYPOINT ["java","-jar","/app/app.jar"]
