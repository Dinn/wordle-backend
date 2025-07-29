## 1. 요약

Docker + Gradle Wrapper만 있으면 **Linux / macOS / Windows** 어디서든 동일한 개발 루프를 돌릴 수 있습니다.

```
git clone ▶ docker compose up --build ▶ 코드 수정 & 저장
                     ▲             │
                     └── exec bash ─┘   (8081 포트에서 핫리로드)
```

---

## 2. 단계별 명령

| 단계                     | Linux / macOS / Windows PowerShell 공통                                                   | 비고                            |
| ---------------------- | --------------------------------------------------------------------------------------- | ----------------------------- |
| ① **필수 설치**            | Docker Desktop (WSL 2 백엔드 ON)<br>Git (CLI)                                              | Java·Gradle는 이미지 안에 포함        |
| ② **소스 클론**            | `bash<br>git clone https://github.com/Dinn/wordle-backend.git<br>cd wordle-backend` |                               |
| ③ **컨테이너 빌드·기동**       | `bash<br>docker compose up --build -d`                                                  | `backend`(8080)·`db`(5432) 기동 |
| ④ **백엔드 기본 로그**        | `bash<br>docker compose logs -f backend`                                                | PID 1 = jar 실행 (8080)         |
| ⑤ **개발용 쉘 진입**         | `bash<br>docker compose exec backend bash`                                              | 컨테이너 내 `/workspace` 진입        |
| ⑥ **핫리로드 서버(8081) 실행** | `bash<br>cd /workspace<br>./gradlew bootRun --args='--server.port=8081'`                | 저장 → 1-2 초 내 리로드              |
| ⑦ **API 테스트**          | 브라우저 / Postman → `http://localhost:8081/actuator/health`                                | 200 OK = 정상                   |
| ⑧ **종료**               | `bash<br>docker compose down`                                                           | 데이터 볼륨은 유지                    |

> **코드 수정 루프**
>
> 1. 호스트 IDE/편집기로 `src/...` 저장
> 2. `bootRun` 터미널에 `Restart completed` 로그 확인
> 3. 브라우저 새로고침 → 변경 반영

---

## 3. OS별 특이점

| OS                  | 체크사항                                                                        |
| ------------------- | --------------------------------------------------------------------------- |
| Linux               | Docker Desktop or Podman OK (포트 방화벽 허용)                                     |
| macOS (Intel/M-시리즈) | 다중 아키 이미지라 추가 설정 X                                                          |
| Windows 10/11       | Docker Desktop → *Use WSL 2 backend* 필수<br>`powershell`·`git bash` 모두 명령 동일 |

---

## 4. 자주 묻는 질문

| Q                       | A                                                                               |
| ----------------------- | ------------------------------------------------------------------------------- |
| 8080 충돌인데 8081 은 열리나요?  | 네. `bootRun` 에 `--server.port=8081` 옵션을 줘 8080 jar, 8081 dev 서버를 병행.            |
| 컨테이너 안에 vim 없어요.        | `apt-get update && apt-get install -y vim` (세션용, 재빌드 시 사라짐)                     |
| DB 스키마 바꿨더니 validate 실패 | `docker/postgres` 폴더에 `00X_new.sql` 추가 → `docker compose down -v && up --build` |
| 로컬 8080 포트도 이미 사용 중     | `docker-compose.yml` `ports` 를 `"8082:8080"` 식으로 바꾸면 호스트 8082 ↔ 컨테이너 8080 매핑    |

---

## 5. 참고 커맨드 모음

```bash
# 컨테이너 목록 조회
docker compose ps

# 실시간 DB 접속
docker exec -it wordle-backend_db_1 psql -U wordle -d wordle

# backend 컨테이너 재빌드만
docker compose build backend

# 이미지·볼륨 완전 삭제 (클린 리셋)
docker compose down -v --rmi all
```

---

