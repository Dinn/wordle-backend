# Wordle Backend

[![CI](https://github.com/<ORG>/wordle-backend/actions/workflows/ci.yml/badge.svg)](https://github.com/<ORG>/wordle-backend/actions/workflows/ci.yml)

Spring Boot 3 + Kotlin 1.9 + Postgres + Redis 로 구현한 Wordle 게임 백엔드.

---

## 🏎 Quick Start
```bash
# 1) 클론
$ git clone https://github.com/<ORG>/wordle-backend.git && cd wordle-backend

# 2) 환경변수 파일 준비
$ cp .env.example .env

# 3) 컨테이너 기동
$ docker compose up --build