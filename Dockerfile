# -------- Build stage --------
FROM ubuntu:24.04 AS build
WORKDIR /app

# 시간대 설정 (상호작용 방지)
ENV DEBIAN_FRONTEND=noninteractive

# OpenJDK 21 설치
RUN apt-get update && apt-get install -y \
    openjdk-21-jdk \
    && rm -rf /var/lib/apt/lists/*

# 프로젝트 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# gradlew 실행 권한 부여
RUN chmod +x gradlew

# 의존성 다운로드 (캐싱 최적화)
RUN ./gradlew dependencies --no-daemon

# 소스코드 복사 및 빌드
COPY src src
RUN ./gradlew clean bootJar -x test --no-daemon

# -------- Runtime stage (JRE 21) --------
FROM ubuntu:24.04
WORKDIR /app

# 시간대 설정 (상호작용 방지)
ENV DEBIAN_FRONTEND=noninteractive

# OpenJDK 21 JRE 설치
RUN apt-get update && apt-get install -y \
    openjdk-21-jre-headless \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]