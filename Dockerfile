############### 1️⃣ Build Stage #######################################
FROM --platform=${BUILDPLATFORM} gradle:8.5-jdk21 AS build

WORKDIR /app

# Copy everything (Docker-layer cache hits on unchanged files)
COPY . .

# [추가 1] gradlew 파일에 실행 권한 부여
RUN chmod +x ./gradlew

# [추가 2] Windows 줄 바꿈 문자(CRLF)를 Unix(LF)로 변환
# dos2unix가 없을 수 있으므로 설치 후 실행합니다.
RUN apt-get update && apt-get install -y dos2unix
RUN dos2unix ./gradlew

# Produce an executable JAR (tests 생략 → 빨리 빌드)
RUN ./gradlew clean bootJar -x test

############### 2️⃣ Runtime Stage #####################################
FROM --platform=${TARGETPLATFORM} eclipse-temurin:21-jre

# 명시적 라이선스 라벨(SBOM · 리뷰 용이)
LABEL org.opencontainers.image.licenses="EPL-2.0 OR GPL-2.0-only WITH Classpath-exception-2.0"

WORKDIR /app

# 빌드한 JAR 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 애플리케이션 포트
EXPOSE 8080

# 컨테이너 시작 시 실행할 명령
ENTRYPOINT ["java","-jar","/app/app.jar"]