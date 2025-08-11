아래 내용을 **README.md** 에 그대로 붙여넣으면 GitHub Markdown에서 문제없이 렌더링됩니다.
(HTML `<br>` 태그 제거 ✔)

````markdown
## 1. 요구 사항
- **Docker Desktop**  
  - Windows 10/11 → WSL 2 백엔드 활성화  
- **Git** (CLI)  
- 추가 설치 불필요 ← JDK·Gradle은 이미지에 포함

---

## 2. 빠른 시작 (Linux / macOS / Windows 공통)

```bash
# 1) 저장소 클론
git clone https://github.com/your-org/wordle-backend.git
cd wordle-backend

# 2) 컨테이너 빌드 및 기동
docker compose up --build -d      # backend(8080) + db(5432)

# 3) 개발용 셸 진입
docker compose exec backend bash  # 컨테이너 안으로

# 4) 핫리로드 서버 실행 (8081)
cd /workspace
./gradlew bootRun --args='--server.port=8081'

# 5) 애플리케이션 확인
curl http://localhost:8081/actuator/health    # {"status":"UP"}
````

**개발 루프**

1. 호스트에서 코드 수정 & 저장
2. 터미널에 `Restart completed` 로그 확인 (1-2 초)
3. 브라우저 / Postman에서 즉시 반영 확인

---

## 3. 자주 쓰는 명령

| 목적             | 명령                                                             |
| -------------- | -------------------------------------------------------------- |
| 컨테이너 상태        | `docker compose ps`                                            |
| backend 로그 스트림 | `docker compose logs -f backend`                               |
| DB CLI 접속      | `docker exec -it wordle-backend_db_1 psql -U wordle -d wordle` |
| backend만 재빌드   | `docker compose build backend`                                 |
| 전체 초기화         | `docker compose down -v --rmi all`                             |

---

## 4. FAQ

| 질문                      | 답변                                                                            |
| ----------------------- | ----------------------------------------------------------------------------- |
| **8080이 이미 사용 중인데?**    | `./gradlew bootRun --args='--server.port=8081'` 명령으로 8081 포트 사용               |
| **컨테이너에서 편집기(vim) 없어요** | `apt-get update && apt-get install -y vim` (세션 한정)                            |
| **스키마 바뀌어 validate 실패** | `docker/postgres` 폴더에 `003_*.sql` 추가 → `docker compose down -v && up --build` |

---

# Wordle Backend 프로젝트 구조 분석

## 📁 프로젝트 구조

`````
