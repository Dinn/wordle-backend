# Wordle Backend

Kotlin + Spring Boot WebFlux 기반 **Wordle(워들) 퍼즐 게임 백엔드**입니다.
팀원이 `git clone` 한 뒤 **10분 이내**에 개발·테스트·디버깅·통합 환경을 구축할 수 있도록 리포지터리 자체에 모든 설정·스크립트·도구를 동봉했습니다.

> 꿀팁   **글 전체를 한 번 훑고 바로 *Quick Start* 섹션**부터 따라 하세요.

---

## ⚙️ Tech Stack

| 계층                 | 선택 기술                         | 이유                       |
| ------------------ | ----------------------------- | ------------------------ |
| Language / Runtime | Kotlin 2.x · JDK 21 (LTS)     | 코루틴·정적 타입 · 최신 LTS       |
| Framework          | Spring Boot 3.3 (WebFlux)     | 논블로킹 I/O · Actuator 헬스체크 |
| Database           | PostgreSQL 16                 | 일일 퍼즐·시도 영속화 (ACID)      |
| Cache              | Redis 7                       | 오늘의 퍼즐 ID / 랭킹 보드 TTL 캐시 |
| Build Tool         | Gradle 8 (Kotlin DSL)         | 증분·캐시·Jib 플러그인           |
| Container          | Docker Compose                | DB·Redis·App 한 줄 기동      |
| Dev Env            | VS Code *Dev Containers* (옵션) | “설치 0” IDE 자동 완성         |
| CI                 | GitHub Actions                | PR 단계 포맷·정적분석·테스트 자동화    |

---

## 🚀 Quick Start (개발 모드)

### 1. 필수 사전 설치

* **Docker** + `docker compose`
* (선택) **VS Code** + *Dev Containers* 확장 ⇢ 컨테이너 내 IDE 디버깅
* **JDK 21** (컨테이너 방식만 쓸 경우 생략 가능)

### 2. 코드 가져오기 & 인프라 기동

```bash
$ git clone https://github.com/your-org/wordle-backend.git
$ cd wordle-backend
$ docker compose up -d     # PostgreSQL + Redis ¥ 기동
```

### 3. 서버 실행

```bash
# 컨테이너 IDE 방식 (VS Code)
① VS Code 열기 → ↗ 상태바 “Reopen in Container” 클릭
② 터미널: ./gradlew bootRun

# 순수 로컬 JDK 방식
$ ./gradlew bootRun --args='--spring.profiles.active=dev'
```

* **Health Check**  [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
* **Swagger UI**    [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

> ✅ 여기까지 200 OK 가 뜨면 개발 준비 완료!

---

## 📂 Project Layout 요약

```text
src/main/kotlin/com/example/wordle/
 ├─ domain/            // 퍼즐·시도 엔티티, 비즈니스 모델
 ├─ application/       // GameService·랭킹 계산
 ├─ infrastructure/    // JPA Repository, Redis DAO
 └─ api/               // REST Controller
```

| 경로                         | 책임                                          |
| -------------------------- | ------------------------------------------- |
| `build.gradle.kts`         | 플러그인·의존성·컨테이너 빌드 정의                         |
| `docker-compose.yml`       | db (5432), redis (6379), app (8080) 한 번에 기동 |
| `.devcontainer/`           | 컨테이너 IDE 설정 (포트포워딩, 확장 자동설치)                |
| `.github/workflows/ci.yml` | PR 단계 `ktlint + detekt + test` 실행           |

---

## 🧑‍💻 일상 작업 명령 모음

```bash
./gradlew test                 # 단위·통합 테스트
./gradlew ktlintFormat         # 코드 자동 포맷
./gradlew detekt               # 정적 분석
./gradlew jibDockerBuild       # Jib → 로컬 도커 이미지 build
./gradlew dependencyUpdates    # 라이브러리 신버전 체크
```

---

## 🌐 브랜치 · 커밋 전략

| 종류         | 규칙                                                 |
| ---------- | -------------------------------------------------- |
|  Main (배포) | `main` – 태그/릴리즈 단계만 직접 푸시                          |
|  Dev 통합    | `dev` – 기능 병합 전용, 24h 내 그린 상태 유지                   |
|  기능        | `feature/<ticket-id>-<short-desc>`                 |
|  커밋 메시지    | Conventional Commits (`feat:`, `fix:`, `chore:` …) |

---

## 🛡️ 테스트 & 품질 게이트 (CI)

| 단계         | 툴                         | 실패 시            |
| ---------- | ------------------------- | --------------- |
|  포맷        | `ktlintCheck`             | 자동 수정 제안        |
|  정적 분석     | `detekt` (≥ 0 Code Smell) | PR 실패           |
|  단위/통합 테스트 | JUnit 5 + Testcontainers  | 커버리지 < 80% 시 경고 |

---

## ❓ Troubleshooting

| 증상                         | 해결책                                                        |
| -------------------------- | ---------------------------------------------------------- |
| `java: command not found`  | JDK 21 설치 or Dev Container 모드로 진입                          |
| `Port 5432 already in use` | 로컬에 돌고 있는 Postgres 중지 → `docker ps`/`docker kill`          |
| 빌드 느림                      | Gradle 캐시 삭제 후 재빌드 `./gradlew clean build --scan` 으로 병목 확인 |

---

## 🤝 Contributing Guide

1. Feature 브랜치 체크아웃
   2. 단위 테스트 추가 후 구현
   3. `./gradlew ktlintFormat detekt test` 통과
   4. PR → 자동 CI Green 되면 리뷰어 지정

---

