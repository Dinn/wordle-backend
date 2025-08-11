#!/usr/bin/env sh
set -e

# 기본값 (Compose 환경변수로 오버라이드 가능)
KEYSTORE_PATH="${KEYSTORE_PATH:-/workspace/src/main/resources/keys/keystore.p12}"
KEY_ALIAS="${KEY_ALIAS:-oauth-key}"
KEYSTORE_PW="${KEYSTORE_PW:-changeit}"
DNAME="${DNAME:-CN=wordle-auth, OU=Dev, O=Wordle, L=Seoul, S=Seoul, C=KR}"
APP_JAR="${APP_JAR:-}"

# APP_JAR 자동 탐색: /app/*.jar → 없으면 /workspace/build/libs/*.jar
if [ -z "$APP_JAR" ]; then
  if ls /app/*.jar >/dev/null 2>&1; then
    APP_JAR="$(ls /app/*.jar | head -n1)"
  elif ls /workspace/build/libs/*.jar >/dev/null 2>&1; then
    APP_JAR="$(ls /workspace/build/libs/*.jar | head -n1)"
  else
    echo "[entrypoint] ERROR: cannot locate application JAR. Set APP_JAR or ensure the JAR is at /app/*.jar or /workspace/build/libs/*.jar"
    exit 1
  fi
fi

# keytool 존재 확인 (런타임 이미지가 JRE라면 없을 수 있음)
if ! command -v keytool >/dev/null 2>&1; then
  echo "[entrypoint] ERROR: keytool not found. Use a JDK base image or install it."
  exit 1
fi

# keystore 없으면 생성
if [ ! -f "$KEYSTORE_PATH" ]; then
  echo "[entrypoint] keystore not found, generating at $KEYSTORE_PATH"
  mkdir -p "$(dirname "$KEYSTORE_PATH")"
  keytool -genkeypair \
    -alias "$KEY_ALIAS" \
    -keyalg RSA -keysize 4096 \
    -storetype PKCS12 \
    -keystore "$KEYSTORE_PATH" \
    -storepass "$KEYSTORE_PW" -keypass "$KEYSTORE_PW" \
    -validity 3650 \
    -dname "$DNAME" \
    -noprompt
fi

# Spring Authorization Server에 file: 경로 오버라이드
export SPRING_SECURITY_OAUTH2_AUTHORIZATIONSERVER_KEY_STORE="file:${KEYSTORE_PATH}"
export SPRING_SECURITY_OAUTH2_AUTHORIZATIONSERVER_KEY_STORE_PASSWORD="${KEYSTORE_PW}"
export SPRING_SECURITY_OAUTH2_AUTHORIZATIONSERVER_KEY_ALIAS="${KEY_ALIAS}"

echo "[entrypoint] Using APP_JAR=$APP_JAR"
exec java -jar "$APP_JAR"