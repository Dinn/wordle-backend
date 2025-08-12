# 🎮 Wordle Backend

Spring Boot 3.3 + Kotlin으로 구축된 OAuth2 인증 서버를 포함한 Wordle 게임 백엔드 API입니다.

> **최근 업데이트**: WebFlux에서 Spring MVC 아키텍처로 마이그레이션하여 OAuth2 Authorization Server와의 호환성을 개선했습니다. (2025.08.12)

## 🛠️ 기술 스택

| 계층 | 기술 | 버전 |
|------|------|------|
| **언어** | Kotlin | 2.0.0 |
| **프레임워크** | Spring Boot MVC | 3.3.0 |
| **인증** | OAuth2 Authorization Server | 1.3.0 |
| **데이터베이스** | PostgreSQL | 16 |
| **ORM** | Spring Data JPA | 3.3.0 |
| **빌드 도구** | Gradle | 8.5 |
| **컨테이너** | Docker + Compose | Latest |
| **테스트** | JUnit 5 + MockMvc | 5.10.2 |

---

## 🚀 빠른 시작

### 1. 요구 사항
- **Docker Desktop** (WSL 2 백엔드 권장)
- **Git CLI**
- **Java 21+** (선택사항 - 로컬 개발 시)

### 2. 설치 및 실행

```bash
# 1) 저장소 클론
git clone <repository-url>
cd wordle-backend

# 2) 환경 변수 확인 (선택사항)
cp .env.example .env  # 환경별 설정 오버라이드

# 3) 전체 스택 실행
docker compose up --build

# 4) 헬스체크 확인
curl http://localhost:8080/actuator/health
# 응답: {"status":"UP"}
```

### 3. 서비스 접근

| 서비스 | URL | 용도 |
|--------|-----|------|
| **Backend API** | http://localhost:8080 | REST API |
| **PostgreSQL** | localhost:5432 | 데이터베이스 |
| **Actuator** | http://localhost:8080/actuator | 모니터링 |
| **OAuth2 JWKS** | http://localhost:8080/oauth2/jwks | JWT 공개키 |

---

## 🔧 개발 환경

### 로컬 개발 (Hot Reload)

```bash
# 1) 컨테이너 내부 접속
docker compose exec backend bash

# 2) 개발 서버 실행 (다른 포트)
cd /workspace
./gradlew bootRun --args='--server.port=8081 --spring.profiles.active=dev'

# 3) 코드 변경 시 자동 재시작 (Spring Boot DevTools)
# 파일 저장 → 1-2초 후 자동 재시작
```

### IDE 설정 (IntelliJ IDEA)

```kotlin
// 1) Import Project → Gradle
// 2) SDK: Eclipse Temurin 21
// 3) Gradle JVM: Project SDK

// 4) Run Configuration 추가
// Main class: com.example.wordle.WordleApplicationKt
// VM options: -Dspring.profiles.active=dev
// Program arguments: --server.port=8081
```

### VS Code + Dev Container

```bash
# 1) VS Code Extensions 설치
# - Dev Containers
# - Kotlin Language Support

# 2) 컨테이너에서 열기
# Ctrl+Shift+P → "Dev Containers: Reopen in Container"

# 3) 통합 터미널에서 바로 개발
./gradlew bootRun
```

---

## 🏗️ 아키텍처

### Spring MVC + JPA 구조

이 프로젝트는 **Spring MVC + JPA** 아키텍처를 사용합니다:

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Controllers   │ -> │    Services     │ -> │  Repositories   │
│  (REST APIs)    │    │ (비즈니스 로직)   │    │   (데이터 액세스) │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         v                       v                       v
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   MockMvc 테스트 │    │   Service 테스트 │    │      JPA        │
│  (API 레이어)    │    │  (단위 테스트)   │    │   (PostgreSQL)  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

#### 장점
- **OAuth2 Authorization Server 완전 호환**: MVC 기반에서 안정적 동작
- **Spring Ecosystem 통합**: 대부분의 Spring 라이브러리와 원활한 연동
- **성숙한 생태계**: 풍부한 문서와 커뮤니티 지원
- **디버깅 용이성**: 동기적 처리로 인한 명확한 실행 흐름

### 주요 컴포넌트

```kotlin
// 1. REST Controllers
@RestController
@RequestMapping("/stats")
class StatsController(private val statsService: StatsService)

// 2. Service Layer  
@Service
@Transactional
class StatsService(private val playerStatsRepository: PlayerStatsRepository)

// 3. JPA Entities
@Entity
@Table(name = "player_stats")
data class PlayerStats(...)

// 4. Repository Layer
@Repository
interface PlayerStatsRepository : JpaRepository<PlayerStats, String>
```

---

## 🧪 테스트

### 전체 테스트 실행

```bash
# 1) 컨테이너 내에서 실행 (권장)
docker compose exec backend bash -c 'cd /workspace && ./gradlew test'

# 2) 로컬에서 실행 (Java 21 필요)
./gradlew test

# 3) 테스트 리포트 확인
# build/reports/tests/test/index.html
```

### 테스트 프레임워크

- **MockMvc**: REST API 통합 테스트
- **@SpringBootTest**: 전체 컨텍스트 로드
- **H2 Database**: 인메모리 테스트 데이터베이스
- **@WithMockUser**: 보안 컨텍스트 모킹

### 카테고리별 테스트

```bash
# 단위 테스트만 (현재 사용 가능)
./gradlew test --tests "*Test"

# 특정 패키지 테스트
./gradlew test --tests "com.example.wordle.stats.*"
./gradlew test --tests "com.example.wordle.auth.*"

# 특정 클래스 테스트
./gradlew test --tests "StatsControllerTest"
./gradlew test --tests "AuthControllerTest"

# 특정 메소드 테스트
./gradlew test --tests "StatsControllerTest.GET stats returns user statistics"
```

### 테스트 커버리지

```bash
# JaCoCo 커버리지 리포트 생성
./gradlew jacocoTestReport

# 리포트 확인
# build/reports/jacoco/test/html/index.html
```

### API 테스트 (수동)

```bash
# 1) 회원가입 테스트
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "TestPass123!"
  }'

# 2) OAuth2 클라이언트 토큰 발급
curl -X POST http://localhost:8080/oauth2/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d 'grant_type=client_credentials&client_id=wordle-client&client_secret=secret'

# 3) JWT 토큰으로 보호된 엔드포인트 접근
curl -H "Authorization: Bearer <ACCESS_TOKEN>" \
  http://localhost:8080/stats
```

---

## 📊 데이터베이스

### 스키마 구조

```sql
-- 사용자 테이블
users (id, username, password, email, enabled, role)

-- OAuth2 테이블
oauth2_registered_client (클라이언트 정보)
oauth2_authorization (인증 정보)
oauth2_authorization_consent (동의 정보)

-- 게임 통계 테이블 (예정)
game_stats (user_id, games_played, games_won, avg_guesses)
```

### 데이터베이스 접속

```bash
# 1) PostgreSQL CLI 접속
docker compose exec db psql -U wordle -d wordle

# 2) 주요 명령어
\l                    # 데이터베이스 목록
\dt                   # 테이블 목록
\d users              # users 테이블 스키마
SELECT * FROM users;  # 사용자 데이터 조회

# 3) 데이터 초기화
docker compose down -v  # 볼륨 삭제
docker compose up       # 재생성
```

### 마이그레이션

```bash
# 1) 새 마이그레이션 파일 생성
# src/main/resources/db/migration/V002__add_game_stats.sql

# 2) 개발 환경에서는 JPA가 자동 생성
# application-dev.yml: ddl-auto: create-drop

# 3) 운영 환경에서는 Flyway 마이그레이션 사용
# application-prod.yml: ddl-auto: validate
```

---

## 🔐 보안 설정

### OAuth2 클라이언트 설정

```kotlin
// 기본 클라이언트 (application.yml에서 설정)
client-id: wordle-client
client-secret: secret
scopes: read, write
grant-types: authorization_code, client_credentials, refresh_token
```

### JWT 토큰 설정

```bash
# 1) Keystore 자동 생성 (Docker 시작 시)
# /workspace/src/main/resources/keys/keystore.p12

# 2) 수동 생성 (개발용)
keytool -genkeypair \
  -alias oauth-key \
  -keyalg RSA -keysize 4096 \
  -storetype PKCS12 \
  -keystore keystore.p12 \
  -storepass changeit \
  -validity 3650 \
  -dname "CN=wordle-auth, OU=Dev, O=Wordle, L=Seoul, C=KR"

# 3) 공개키 확인
curl http://localhost:8080/oauth2/jwks
```

### 환경별 설정

```yaml
# application-dev.yml (개발)
spring:
  jpa:
    hibernate.ddl-auto: create-drop
    show-sql: true
logging:
  level:
    org.springframework.security: DEBUG

# application-prod.yml (운영)
spring:
  jpa:
    hibernate.ddl-auto: validate
    show-sql: false
logging:
  level:
    org.springframework.security: INFO
```

---

## � 변경 히스토리

### v2.0.0 (2025.08.12) - Spring MVC 마이그레이션
- **아키텍처 변경**: WebFlux → Spring MVC + JPA
- **테스트 프레임워크 변경**: WebTestClient → MockMvc
- **OAuth2 호환성 개선**: Authorization Server 안정성 향상
- **빌드 최적화**: Detekt 플러그인 임시 제거 (Kotlin 2.0 호환성)

### v1.0.0 - 초기 릴리스
- Spring Boot 3.3 + Kotlin 2.0 기반 구조
- OAuth2 Authorization Server 통합
- PostgreSQL + JPA 데이터 계층
- Docker Compose 개발 환경

---

## �🐛 트러블슈팅

### 자주 발생하는 문제

#### 1. 포트 충돌
```bash
# 문제: 8080 포트가 이미 사용 중
# 해결: 다른 포트 사용
docker compose exec backend ./gradlew bootRun --args='--server.port=8081'

# 또는 docker-compose.yml 수정
ports:
  - "8081:8080"
```

#### 2. 데이터베이스 연결 실패
```bash
# 문제: 'Connection refused' 또는 스키마 오류
# 해결: 컨테이너 순서 확인
docker compose logs db
docker compose logs backend

# 완전 초기화
docker compose down -v --rmi all
docker compose up --build
```

#### 3. Kotlin 컴파일러 권한 오류
```bash
# 문제: '.kotlin/sessions/' 권한 거부
# 해결: .gitignore에 추가 후 삭제
echo ".kotlin/" >> .gitignore
sudo rm -rf .kotlin/
git rm --cached -r .kotlin/ 2>/dev/null || true
```

#### 4. OAuth2 keystore 오류
```bash
# 문제: 'keystore not found' 또는 'keytool not found'
# 해결: JDK 이미지 사용 + 자동 생성 스크립트

# entrypoint.sh 실행 권한 확인
chmod +x docker/entrypoint.sh

# 수동 keystore 생성
docker compose exec backend keytool -genkeypair -alias oauth-key ...
```

#### 5. 테스트 실패
```bash
# 문제: H2 데이터베이스 또는 OAuth2 설정 오류
# 해결: 테스트용 keystore 확인
ls src/test/resources/test-keystore.p12

# 테스트만 실행 (프로덕션 DB 제외)
./gradlew test -Dspring.profiles.active=test
```

### 로그 확인

```bash
# 1) 전체 로그
docker compose logs

# 2) 특정 서비스 로그
docker compose logs -f backend
docker compose logs -f db

# 3) 실시간 로그 스트림
docker compose logs -f --tail=100

# 4) 애플리케이션 로그 레벨 조정
# application-dev.yml
logging:
  level:
    com.example.wordle: DEBUG
    org.springframework.security: TRACE
```

### 성능 최적화

```bash
# 1) Gradle 빌드 캐시 확인
docker compose exec backend ./gradlew --build-cache build

# 2) Docker 이미지 크기 최적화
docker images | grep wordle

# 3) 메모리 사용량 확인
docker stats wordle-backend-backend-1

# 4) JVM 힙 덤프 (메모리 누수 확인)
docker compose exec backend jcmd 1 GC.run_finalization
```

---

## 📚 API 문서

### 주요 엔드포인트

| 메소드 | 경로 | 설명 | 인증 |
|--------|------|------|------|
| `POST` | `/auth/signup` | 회원가입 | 없음 |
| `POST` | `/oauth2/token` | 토큰 발급 | Client |
| `GET` | `/oauth2/jwks` | JWT 공개키 | 없음 |
| `GET` | `/stats` | 게임 통계 | Bearer |
| `POST` | `/stats` | 통계 업데이트 | Bearer |
| `GET` | `/actuator/health` | 헬스체크 | 없음 |

### 응답 예시

```json
// POST /auth/signup
{
  "username": "testuser",
  "email": "test@example.com",
  "message": "회원가입이 완료되었습니다"
}

// GET /oauth2/jwks
{
  "keys": [
    {
      "kty": "RSA",
      "kid": "...",
      "n": "...",
      "e": "AQAB"
    }
  ]
}

// GET /stats
{
  "gamesPlayed": 10,
  "gamesWon": 8,
  "currentStreak": 3,
  "maxStreak": 5,
  "guessDistribution": {
    "1": 1,
    "2": 3,
    "3": 2,
    "4": 2,
    "5": 0,
    "6": 0
  }
}
```

---

## 🚢 배포

### Docker 프로덕션 빌드

```bash
# 1) 멀티 아키텍처 빌드
docker buildx build --platform linux/amd64,linux/arm64 -t wordle-backend:latest .

# 2) 프로덕션 환경 변수
export SPRING_PROFILES_ACTIVE=prod
export SPRING_DATASOURCE_URL=jdbc:postgresql://prod-db:5432/wordle
export SPRING_DATASOURCE_PASSWORD=secure_password

# 3) 프로덕션 실행
docker run -d \
  --name wordle-backend \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  wordle-backend:latest
```

### CI/CD 파이프라인

```yaml
# .github/workflows/ci.yml
name: CI/CD
on:
  push:
    branches: [main]
  pull_request:

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: docker/setup-buildx-action@v3
      - name: Run Tests
        run: docker compose -f docker-compose.yml up --build --exit-code-from backend
```

---

## 🤝 기여하기

1. **Fork** 후 **Clone**
2. **Feature Branch** 생성: `git checkout -b feature/amazing-feature`
3. **코드 스타일** 준수: `./gradlew ktlintCheck`
4. **테스트 추가**: 커버리지 80% 이상 유지
5. **커밋**: `git commit -m 'feat: Add amazing feature'`
6. **Push**: `git push origin feature/amazing-feature`
7. **Pull Request** 생성

### 코드 스타일

```bash
# Kotlin 린터 확인
./gradlew ktlintCheck

# 자동 포맷팅
./gradlew ktlintFormat

# 테스트 커버리지 확인 (최소 80%)
./gradlew jacocoTestCoverageVerification
```

---

## 📄 라이선스

이 프로젝트는 [MIT License](LICENSE)로 배포됩니다.

---

## 📞 지원

- **GitHub Issues**: 버그 리포트 및 기능 요청
- **Discussion**: 질문 및 아이디어 공유
- **Wiki**: 상세한 아키텍처 문서

---

**Happy Coding! 🎯**
