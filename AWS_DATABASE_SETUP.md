# AWS 데이터베이스 연결 가이드

## 📋 개요

로컬 PostgreSQL에서 AWS RDS로 데이터베이스를 마이그레이션하기 위한 설정 가이드입니다.

## 🏗️ 환경별 구성

### 1. 개발/테스트 환경 (Dev)
- **DB Name**: `wordle_dev`
- **User**: `wordle_dev_user`
- **포트**: `5432`
- **용도**: 개발자 로컬 개발, 기능 테스트

### 2. 프로덕션 환경 (Prod)
- **DB Name**: `wordle_prod`
- **User**: `wordle_prod_user`
- **포트**: `5432`
- **용도**: 실제 서비스 운영

## 🚀 설정 방법

### Step 1: 환경변수 설정

#### 개발 환경
```bash
# .env.dev 파일 생성
cp .env.dev.template .env.dev

# 실제 AWS 정보로 수정
AWS_DEV_DB_URL=jdbc:postgresql://your-dev-rds-endpoint.region.rds.amazonaws.com:5432/wordle_dev
AWS_DEV_DB_USERNAME=wordle_dev_user
AWS_DEV_DB_PASSWORD=your_dev_password_here
```

#### 프로덕션 환경
```bash
# .env.prod 파일 생성
cp .env.prod.template .env.prod

# 실제 AWS 정보로 수정
AWS_PROD_DB_URL=jdbc:postgresql://your-prod-rds-endpoint.region.rds.amazonaws.com:5432/wordle_prod
AWS_PROD_DB_USERNAME=wordle_prod_user
AWS_PROD_DB_PASSWORD=your_secure_prod_password_here
```

### Step 2: 애플리케이션 실행

#### Docker Compose 사용 (개발)
```bash
# AWS 연결용 Docker Compose 사용
docker-compose -f docker-compose.aws.yml --env-file .env.dev up

# 로컬 DB도 함께 사용하려면
docker-compose -f docker-compose.aws.yml --profile local-db --env-file .env.dev up
```

#### 직접 실행 (IntelliJ/VS Code)
```bash
# 개발 환경
export SPRING_PROFILES_ACTIVE=dev
source .env.dev
./gradlew bootRun

# 프로덕션 환경
export SPRING_PROFILES_ACTIVE=prod
source .env.prod
./gradlew bootRun
```

## 📊 환경별 차이점

| 구분 | 개발 환경 | 프로덕션 환경 |
|------|-----------|---------------|
| DDL 정책 | `update` | `validate` |
| SQL 로깅 | 활성화 | 비활성화 |
| 커넥션 풀 | 10개 | 20개 |
| 헬스체크 | 상세 정보 | 기본 정보만 |
| 로그 레벨 | DEBUG | WARN |

## 🔐 보안 고려사항

### 환경변수 보안
- `.env.*` 파일은 **절대 Git에 커밋하지 마세요**
- `.gitignore`에 이미 추가되어 있습니다
- 프로덕션 환경에서는 AWS Secrets Manager 사용 권장

### 데이터베이스 보안
- RDS 보안 그룹에서 필요한 IP만 허용
- SSL 연결 강제 설정
- 정기적인 비밀번호 변경

## 🔧 트러블슈팅

### 연결 실패
```bash
# 1. 보안 그룹 확인
# AWS Console > RDS > 보안 그룹에서 포트 5432 인바운드 규칙 확인

# 2. 네트워크 연결 테스트
telnet your-rds-endpoint 5432

# 3. 데이터베이스 접속 테스트
psql -h your-rds-endpoint -p 5432 -U wordle_dev_user -d wordle_dev
```

### 성능 문제
```bash
# 연결 풀 모니터링
curl http://localhost:8080/actuator/metrics/hikaricp.connections

# 로그 확인
docker-compose logs backend | grep -i hikari
```

## 📈 모니터링

### 헬스체크 엔드포인트
- **개발**: `http://localhost:8080/actuator/health`
- **프로덕션**: `https://your-domain.com/actuator/health`

### 메트릭 확인
```bash
# 데이터베이스 연결 상태
curl http://localhost:8080/actuator/health/db

# Hikari 커넥션 풀 상태
curl http://localhost:8080/actuator/metrics/hikaricp.connections.active
```

## 🔄 마이그레이션 체크리스트

- [ ] AWS RDS 인스턴스 생성 및 설정 완료
- [ ] 보안 그룹 인바운드 규칙 설정
- [ ] `.env.dev` 파일 생성 및 설정
- [ ] `.env.prod` 파일 생성 및 설정
- [ ] 로컬에서 개발 환경 연결 테스트
- [ ] 데이터베이스 스키마 마이그레이션 실행
- [ ] 애플리케이션 기능 테스트
- [ ] 프로덕션 환경 배포 및 테스트

---

## 📝 주요 파일

- `application-dev.yml`: 개발 환경 설정
- `application-prod.yml`: 프로덕션 환경 설정
- `docker-compose.aws.yml`: AWS 연결용 Docker Compose
- `.env.dev.template`: 개발 환경변수 템플릿
- `.env.prod.template`: 프로덕션 환경변수 템플릿
