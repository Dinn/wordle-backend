# 🎮 Wordle Backend

Spring Boot 3.3 + Kotlin으로 구축된 OAuth2 인증 서버를 포함한 Wordle 게임 백엔드 API입니다.

## 🚀 환경 구축

### 1. 환경변수 설정

```bash
# 환경변수 템플릿 파일 복사
cp .env.template .env.dev (prod, test 동일)

# .env.dev 파일에서 다음 항목들 수정
```

**수정 필요한 항목들:**

```bash
# AWS RDS 설정 (실제 값으로 변경)
AWS_DEV_DB_URL=DB_URL
AWS_DEV_DB_USERNAME=DB_USERNAME  
AWS_DEV_DB_PASSWORD=DB_PASSWORD


### 2. Docker Compose 실행

```bash
# 백그라운드로 컨테이너 실행(prod, test는 env file 이름만 바꿔서)
docker compose --env-file .env.dev up --build -d

# 로그 확인
docker compose logs -f backend

# 헬스체크 확인
curl http://localhost:8080/actuator/health
```

---

## 🔧 개발 환경

### Backend 컨테이너 접속

```bash
# 컨테이너 내부 접속
docker compose exec backend bash

# 작업 디렉토리로 이동
cd /workspace
```

### 핫 리로드 설정

```bash
# 컨테이너 내부에서 개발 서버 실행 (다른 포트 사용)
./gradlew bootRun --args='--server.port=8081 --spring.profiles.active=dev'

# 파일 저장 시 자동 재시작됨 (Spring Boot DevTools)
# 브라우저에서 http://localhost:8081 접속
```

---

## 🗄️ 데이터베이스 접속

### 컨테이너 내부에서 PostgreSQL 접속

```bash
# 1. Backend 컨테이너 접속
docker compose exec backend bash

# 2. PostgreSQL 클라이언트로 AWS RDS 접속
psql -h RDS_ENDPOINT \
     -p 5432 \
     -U DB_USERNAME \
     -d DATABASE_NAME

# 패스워드 입력: DB_PASSWORD
```

**실제 사용 시 대체할 값들:**
- `RDS_ENDPOINT` → `.env.dev` 파일의 AWS RDS 엔드포인트
- `DB_USERNAME` → `.env.dev` 파일의 데이터베이스 사용자명  
- `DATABASE_NAME` → `.env.dev` 파일의 데이터베이스명
- `DB_PASSWORD` → `.env.dev` 파일의 데이터베이스 비밀번호
