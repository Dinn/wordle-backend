# -------- Build stage --------
FROM ubuntu:24.04 AS build
WORKDIR /app

# 시간대 설정 (상호작용 방지)
ENV DEBIAN_FRONTEND=noninteractive

# OpenJDK 21 설치
RUN apt-get update && apt-get install -y \
    openjdk-21-jdk \
    && rm -rf /var/lib/apt/lists/*

COPY . .
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