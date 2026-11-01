# 5주차 BE: 화면과 API를 처음 연결하기 (영화 평점 서비스 + CORS)

> 한 줄 요약: **화면(5173)이 서버(8080)의 API를 직접 부르려면, 서버가 CORS로 "이 출처는 믿어도 돼"라고 허락해야 한다.** 이번 주부터 영화 평점 서비스(`movie/`)를 새로 시작한다.

## 1. 핵심 개념

| 개념 | 쉬운 설명 | 비유 |
|---|---|---|
| **출처 (Origin)** | 프로토콜 + 호스트 + **포트**. `localhost:5173`과 `localhost:8080`은 다른 출처 | 같은 건물이라도 층이 다르면 다른 사무실 |
| **SOP (동일 출처 정책)** | 브라우저는 다른 출처의 응답을 JS가 읽지 못하게 막는다 | 다른 사무실 서류는 열람 금지 |
| **CORS** | 서버가 `Access-Control-Allow-Origin` 헤더로 특정 출처를 허락 | 다른 사무실이 발급한 출입증 |
| **Preflight (OPTIONS)** | 본 요청 전에 "이 방식으로 보내도 돼?"를 먼저 묻는 요청 | 방문 전 전화로 확인 |
| **CORS는 브라우저 규칙** | Postman은 브라우저가 아니라 CORS 설정 없이도 응답을 받는다 | 출입증 검사는 건물 경비원(브라우저)만 함 |
| **`WebMvcConfigurer.addCorsMappings`** | 스프링에서 허용 출처·메서드·헤더를 등록 | 출입증 발급 규칙표 |
| **API 명세** | 기능 / Method / URL / 요청 / 응답을 표로 약속 | 프론트·백 공용 계약서 |
| **`@MappedSuperclass` (BaseTimeEntity)** | 여러 엔티티가 공통 필드(`createdAt`, `updatedAt`)를 상속 | 모든 서류에 찍는 공통 날짜 도장 |
| **`@CreationTimestamp` / `@UpdateTimestamp`** | 저장·수정 시각을 Hibernate가 자동으로 채움 | 자동 날짜 도장 |
| **`existsBy...`** | "있는지만" 확인하는 쿼리 메서드 → boolean | 명단에 이름 있나 확인 |
| **`ResponseStatusException`** | 예외 하나로 상태 코드(404)와 메시지를 바로 응답 | 즉석 안내문 |

### API 명세
| 기능 | Method | URL | 응답 |
|---|---|---|---|
| 닉네임 중복 체크 | GET | `/members/nickname/{nickname}` | `{ "available": true }` |
| 이메일 중복 체크 | GET | `/members/email/{email}` | `{ "available": true }` |
| 유저별 평점 조회 | GET | `/members/{memberId}/ratings` | `[{ ratingId, movieId, score, comment, createdAt, updatedAt }]` (최신순), 없는 유저 404 |

## 2. 이번 주 실습 → 파일 위치 (`movie/`)

| 워크북 단원 | 실습 | 파일 |
|---|---|---|
| 본문 전 2 | ERD 스키마(member·rating·favorite)와 더미 데이터 | [schema.sql](../../movie/sql/schema.sql), [seed.sql](../../movie/sql/seed.sql) |
| 본문 전 3 | 프로젝트 생성 (Web, JPA, Validation, MySQL, Lombok), DB `umc_movie` | [build.gradle](../../movie/build.gradle), [application.yml](../../movie/src/main/resources/application.yml) |
| 실습 0 | CORS 에러 확인용 간이 프론트 | [mock-frontend/index.html](../../mock-frontend/index.html) |
| 3 | 엔티티 | [BaseTimeEntity](../../movie/src/main/java/com/umc/movie/entity/BaseTimeEntity.java), [Member](../../movie/src/main/java/com/umc/movie/entity/Member.java), [Rating](../../movie/src/main/java/com/umc/movie/entity/Rating.java) |
| 4 | Repository | [MemberRepository](../../movie/src/main/java/com/umc/movie/repository/MemberRepository.java), [RatingRepository](../../movie/src/main/java/com/umc/movie/repository/RatingRepository.java) |
| 5 | 응답 DTO | [src/main/java/com/umc/movie/dto/](../../movie/src/main/java/com/umc/movie/dto) |
| 6·7 | 닉네임·이메일 중복 체크 | [MemberService](../../movie/src/main/java/com/umc/movie/service/MemberService.java), [MemberController](../../movie/src/main/java/com/umc/movie/controller/MemberController.java) |
| 8 | 유저별 평점 조회 + 없는 유저 404 | [RatingService](../../movie/src/main/java/com/umc/movie/service/RatingService.java), [RatingController](../../movie/src/main/java/com/umc/movie/controller/RatingController.java) |
| 9 | CORS 설정 | [WebConfig](../../movie/src/main/java/com/umc/movie/config/WebConfig.java) |
| 검증 | H2 테스트 7개 (중복 체크, 최신순, 404, CORS 허용/거절) | [MemberRatingApiTest](../../movie/src/test/java/com/umc/movie/MemberRatingApiTest.java) |

### 필수 미션: 실제 화면에서 확인하기
| 키워드 | 구현 |
|---|---|
| CORS 설정 후 실제 웹 화면에서 호출 | FE 레포 `umcine`의 **`/members` 회원 API 화면** (`umc-web-fe` 5주차: `src/pages/members/member-api-page.tsx`) |
| 현재 구현한 모든 API 연결 | 닉네임 중복, 이메일 중복, 유저별 평점 — 세 API 모두 화면에서 호출 |
| 허용 출처 | `allowedOrigins("http://localhost:5173")` — `"*"` 대신 프론트 개발 서버만 |

### 실행 방법
1. MySQL에서 `movie/sql/schema.sql` → `seed.sql` 실행 (DB `umc_movie`)
2. IntelliJ로 `movie` 폴더 열기 → 실행 구성 → 환경 변수 `DB_PASSWORD=본인비밀번호` → `MovieApplication` 실행
3. CORS 에러 보기(실습 0): `WebConfig`를 잠시 주석 처리 → `mock-frontend` 폴더에서 `npx serve -l 5173` → 버튼 클릭 → Console 에러 확인 → 주석 해제 후 다시 확인
4. 실제 화면: FE `umcine`에서 `.env.local`의 `VITE_UMC_API_BASE_URL=http://localhost:8080` → `pnpm dev` → `http://localhost:5173/members`

## 3. 자주 하는 실수
- Postman에서는 되는데 브라우저에서만 실패 → CORS는 브라우저만 검사한다. 서버 설정을 확인.
- `allowedOrigins("http://localhost:5173/")`처럼 끝에 `/` → 출처 문자열이 달라 허용되지 않는다.
- 프론트 포트가 5174로 뜸(5173 사용 중) → 허용 출처와 달라서 CORS 에러.
- `password`를 응답 DTO에 넣기 → 절대 응답하지 않는다(6주차에 bcrypt 해시 저장).
- 평점 0개 회원과 없는 회원을 둘 다 `[]`로 응답 → 화면이 구분할 수 없다. 없는 회원은 404.

## 4. 스스로 점검하기
<details><summary>Q1. 서버 로그에는 요청이 200으로 찍혔는데 브라우저는 CORS 에러가 나는 이유는?</summary>

서버는 정상 응답했지만, 응답에 `Access-Control-Allow-Origin`이 없어서 브라우저가 JS에 응답을 넘겨주지 않았다.
</details>
<details><summary>Q2. <code>allowedOrigins("*")</code> 대신 특정 출처만 허용하는 이유는?</summary>

아무 사이트나 사용자의 브라우저를 통해 우리 API를 호출하고 응답을 읽을 수 있게 되기 때문. 쿠키·인증을 쓰는 순간 `*`는 허용되지도 않는다.
</details>
