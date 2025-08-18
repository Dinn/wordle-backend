# 환경별 데이터베이스 설정 가이드

## 📋 환경 구성

### 🗄️ 데이터베이스 서버 구조
```
📦 AWS RDS 서버 구조
├── 🔧 DEV/TEST 서버 (공용 - test-wordle-db.cmaym1pniwfj.ap-northeast-2.rds.amazonaws.com)
│   ├── wordle_dev  (개발용 데이터베이스)
│   └── wordle_test (테스트용 데이터베이스)
└── 🏭 PROD 서버 (별도 - your-prod-rds-endpoint.region.rds.amazonaws.com)
    └── wordle_prod (프로덕션용 데이터베이스)
```

## 🚀 환경별 실행 방법

### 1. 개발 환경 (Development)
```bash
# Docker Compose 사용
docker compose -f docker-compose.aws.yml --env-file .env.dev up

# 직접 실행
export SPRING_PROFILES_ACTIVE=dev
source .env.dev
./gradlew bootRun
```

### 2. 테스트 환경 (Test)
```bash
# Docker Compose 사용
docker compose -f docker-compose.aws.yml --env-file .env.test up

# 직접 실행
export SPRING_PROFILES_ACTIVE=test
source .env.test
./gradlew bootRun
```

### 3. 프로덕션 환경 (Production)
```bash
# Docker Compose 사용
docker compose -f docker-compose.aws.yml --env-file .env.prod up

# 직접 실행
export SPRING_PROFILES_ACTIVE=prod
source .env.prod
./gradlew bootRun
```

## 📊 환경별 설정 차이점

| 구분 | DEV | TEST | PROD |
|------|-----|------|------|
| **데이터베이스 서버** | 공용 AWS RDS | 공용 AWS RDS | 별도 AWS RDS |
| **데이터베이스명** | wordle_dev | wordle_test | wordle_prod |
| **DDL 정책** | update | create-drop | validate |
| **SQL 로깅** | 활성화 | 활성화 | 비활성화 |
| **커넥션 풀** | 10개 | 5개 | 20개 |
| **로그 레벨** | DEBUG | DEBUG | WARN |
| **HTTPS** | 비활성화 | 비활성화 | 필수 |

## 🔧 설정 파일 구조

```
📁 환경 설정 파일
├── .env.dev          # 개발 환경 변수
├── .env.test         # 테스트 환경 변수  
├── .env.prod         # 프로덕션 환경 변수
├── application-dev.yml   # 개발 설정
├── application-test.yml  # 테스트 설정
└── application-prod.yml  # 프로덕션 설정
```

## 🛠️ 환경별 데이터베이스 초기화

### DEV 환경
- **정책**: `hibernate.ddl-auto=update`
- **특징**: 스키마 자동 업데이트, 데이터 유지

### TEST 환경  
- **정책**: `hibernate.ddl-auto=create-drop`
- **특징**: 테스트 시작시 생성, 종료시 삭제

### PROD 환경
- **정책**: `hibernate.ddl-auto=validate`
- **특징**: 스키마 검증만, 수동 마이그레이션 필요

## 🔐 보안 설정

### DEV/TEST
- HTTP 허용
- 상세 에러 정보 노출
- 디버그 로깅 활성화

### PROD
- HTTPS 필수
- 에러 정보 최소화
- 로깅 최소화
- SSL 인증서 필요

## 📝 주의사항

1. **환경변수 파일 보안**
   - `.env.*` 파일은 Git에 커밋하지 마세요
   - 실제 비밀번호와 엔드포인트 정보를 입력해야 합니다

2. **데이터베이스 분리**
   - DEV와 TEST는 같은 서버의 다른 데이터베이스
   - PROD는 완전히 별도의 서버 사용

3. **마이그레이션**
   - PROD 환경에서는 수동으로 데이터베이스 스키마 관리
   - Flyway 또는 Liquibase 사용 권장
