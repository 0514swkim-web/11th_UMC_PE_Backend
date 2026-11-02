# 6주차 BE: 입력한 데이터를 저장하기 (회원가입 + 평점 등록/수정/삭제)

> 한 줄 요약: **서버는 입력을 믿지 않는다.** 요청 DTO에서 검증하고(400), 중복은 막고(409), 비밀번호는 bcrypt 해시로만 저장하고, 남의 데이터는 건드리지 못하게(403) 한 뒤, 성공·실패 모두 **같은 모양(`ApiResponse`)** 으로 응답한다.

## 1. 핵심 개념

| 개념 | 쉬운 설명 | 비유 |
|---|---|---|
| **Bean Validation (`@NotBlank`, `@Email`, `@Size`, `@Pattern`, `@Min`, `@Max`)** | 요청 DTO 필드에 규칙을 붙여 둔다 | 신청서 칸마다 적힌 작성 규칙 |
| **`@Valid`** | Controller 파라미터에 붙여야 위 규칙 검사가 실제로 실행된다 | 접수 직원에게 "신청서 확인해 주세요" 버튼 |
| **bcrypt / `PasswordEncoder`** | 비밀번호를 되돌릴 수 없는 60자 해시(`$2a$10$...`)로 바꿔 저장 | 문서 파쇄기. 원래 종이로 못 돌린다 |
| **salt** | 해시마다 섞는 랜덤 값. 같은 비밀번호도 매번 다른 해시가 나온다 | 파쇄할 때마다 다른 색 종이를 섞음 |
| **`spring-security-crypto`** | Security 전체 말고 암호화 기능만 가져오는 가벼운 의존성 | 보안 회사 통째로 말고 파쇄기만 구입 |
| **응답 DTO (`MemberResponse`)** | 엔티티를 그대로 내보내지 않고 필요한 값만 옮겨 담음 → password 노출 차단 | 회원증에는 비밀번호를 안 적음 |
| **`ApiResponse<T>`** | `{ isSuccess, code, message, result }` 공통 봉투 | 양식이 정해진 택배 상자 + 송장 |
| **`@RestControllerAdvice` + `@ExceptionHandler`** | 어디서 예외가 나든 한곳에서 같은 형식으로 바꿔 응답 | 건물 전체 안내 방송실 |
| **201 Created / 204 No Content** | 새로 만들었음 / 성공했지만 돌려줄 본문 없음(삭제) | "접수 완료" 도장 / "처리 완료, 서류 없음" |
| **400 / 403 / 404 / 409** | 입력이 틀림 / 남의 것이라 금지 / 없음 / 이미 있음(중복) | 반려 / 출입 금지 / 그런 방 없음 / 이미 예약됨 |
| **UNIQUE 제약과 동시성** | Service의 `existsBy` 확인을 두 요청이 동시에 통과할 수 있다 → DB UNIQUE가 마지막 안전망(→ 409) | 창구 확인 + 금고 자물쇠 이중 잠금 |
| **`@Builder`** | `Rating.builder().member(..).score(..).build()`처럼 이름표를 붙여 객체 생성 | 칸마다 이름표 있는 주문서 |
| **변경 감지 (Dirty Checking)** | `@Transactional` 안에서 엔티티 값을 바꾸면 `save` 없이도 끝날 때 UPDATE | 고친 서류를 퇴근할 때 자동 제출 |
| **소유자 확인 (`findOwnedRating`)** | 먼저 찾고(없으면 404), 주인이 다르면 403 | 사물함 번호 확인 → 이름표 확인 |

### API 명세 (6주차에 추가)
| 기능 | Method | URL | 요청 | 성공 | 실패 |
|---|---|---|---|---|---|
| 회원가입 | POST | `/members` | `{ email, password, nickname }` | 201 `result: { memberId, email, nickname }` | 400 검증, 409 이메일·닉네임 중복 |
| 평점 등록 | POST | `/members/{memberId}/ratings` | `{ movieId, score, comment? }` | 201 `result: ratingId` | 400 별점 범위, 404 없는 회원, 409 같은 영화 중복 |
| 평점 수정 | PATCH | `/members/{memberId}/ratings/{ratingId}` | `{ score, comment? }` | 200 `result: null` | 400, 403 남의 평점, 404 없는 평점 |
| 평점 삭제 | DELETE | `/members/{memberId}/ratings/{ratingId}` | 없음 | 204 (본문 없음) | 403, 404 |

실패 응답은 모두 같은 모양: `{ "isSuccess": false, "code": "COMMON4001", "message": "...", "result": null }`

| code | 상태 | 언제 |
|---|---|---|
| `COMMON4001` | 400 | `@Valid` 검증 실패 (첫 번째 규칙의 message) |
| `COMMON4092` | 409 | Service가 미리 확인한 중복 (이메일·닉네임·같은 영화 평점) |
| `COMMON4091` | 409 | DB UNIQUE 제약 위반 (동시 요청 안전망) |
| `COMMON403` / `COMMON404` | 403 / 404 | `ResponseStatusException` (남의 평점 / 없는 회원·평점) |

## 2. 이번 주 실습 → 파일 위치 (`movie/`)

| 워크북 단원 | 실습 | 파일 |
|---|---|---|
| 본문 1 | 회원가입 입력 규칙 | [SignUpRequest](../../movie/src/main/java/com/umc/movie/dto/SignUpRequest.java) |
| 실습 1 | bcrypt 의존성 + PasswordEncoder Bean | [build.gradle](../../movie/build.gradle), [PasswordConfig](../../movie/src/main/java/com/umc/movie/config/PasswordConfig.java) |
| 실습 1 | 응답 DTO (password 제외) | [MemberResponse](../../movie/src/main/java/com/umc/movie/dto/MemberResponse.java) |
| 실습 1 | `POST /members`, 중복 확인 → 해시 → 저장 | [Member](../../movie/src/main/java/com/umc/movie/entity/Member.java) `@Builder`, [MemberService](../../movie/src/main/java/com/umc/movie/service/MemberService.java), [MemberController](../../movie/src/main/java/com/umc/movie/controller/MemberController.java) |
| 실습 2 | 공통 응답 + 전역 예외 처리 | [ApiResponse](../../movie/src/main/java/com/umc/movie/dto/ApiResponse.java), [GlobalExceptionHandler](../../movie/src/main/java/com/umc/movie/controller/GlobalExceptionHandler.java) |
| 실습 3 | 평점 요청 DTO (score 1~5, comment 500자) | [CreateRatingRequest](../../movie/src/main/java/com/umc/movie/dto/CreateRatingRequest.java), [UpdateRatingRequest](../../movie/src/main/java/com/umc/movie/dto/UpdateRatingRequest.java) |
| 실습 3 | 평점 등록/수정/삭제 + 소유자 확인 | [Rating](../../movie/src/main/java/com/umc/movie/entity/Rating.java) `@Builder`·`update`, [RatingRepository](../../movie/src/main/java/com/umc/movie/repository/RatingRepository.java) `existsByMember_MemberIdAndMovieId`, [RatingService](../../movie/src/main/java/com/umc/movie/service/RatingService.java), [RatingController](../../movie/src/main/java/com/umc/movie/controller/RatingController.java) |
| 확인 | Postman 컬렉션 | [week06.postman_collection.json](../../movie/postman/week06.postman_collection.json) |
| 검증 | H2 테스트 7개 (201·400·409, 해시 저장, 수정 200·삭제 204, 404·403) | [SignUpRatingWriteApiTest](../../movie/src/test/java/com/umc/movie/SignUpRatingWriteApiTest.java) |

### 실행 방법
1. IntelliJ로 `movie` 폴더 열기 → `build.gradle` 변경 후 Gradle 새로고침 → `MovieApplication` 실행 (환경 변수 `DB_PASSWORD`)
2. Postman에서 `movie/postman/week06.postman_collection.json` Import → 위에서부터 순서대로 Send (평점 등록 응답의 `ratingId`가 컬렉션 변수에 자동 저장되어 수정·삭제에 쓰임)
3. DB 확인: `SELECT member_id, email, password FROM member;` → 새 회원의 password가 `$2a$10$...` 60자 해시인지 확인

## 3. 자주 하는 실수
- `spring-boot-starter-security`를 추가 → 모든 API가 로그인 화면(401)으로 막힌다. 해시만 필요하면 `spring-security-crypto`.
- DTO에 규칙만 붙이고 Controller에 `@Valid`를 빼먹음 → 검증이 실행되지 않는다.
- 엔티티를 그대로 응답 → `password`가 나간다. 응답은 DTO로.
- `existsBy`로 중복을 확인했으니 DB UNIQUE는 필요 없다고 생각 → 동시 요청은 둘 다 통과할 수 있다. UNIQUE + `DataIntegrityViolationException` 처리로 409.
- 수정·삭제에서 평점이 있는지만 확인 → 남의 평점도 바뀐다. 주인(`memberId`)까지 비교해서 403.
- 삭제 응답에 본문을 넣고 204 → 204는 본문이 없어야 한다.
- 이미 있는 파일(`MemberService` 등)을 "새로 만들기"로 만들려 함 → "이미 있습니다" 오류. 기존 파일을 열어서 수정.

## 4. 스스로 점검하기
<details><summary>Q1. 비밀번호를 암호화(encrypt)가 아니라 해시로 저장하는 이유는?</summary>

암호화는 키가 있으면 되돌릴 수 있지만 해시는 되돌릴 수 없다. 서버도 원래 비밀번호를 알 필요가 없고, 로그인 때는 입력값을 `matches()`로 해시와 비교하면 된다. DB가 유출돼도 원래 비밀번호가 바로 드러나지 않는다.
</details>
<details><summary>Q2. 같은 이메일로 거의 동시에 두 번 가입 요청이 오면?</summary>

두 요청 모두 `existsByEmail`에서 `false`를 받고 저장까지 갈 수 있다. 두 번째 INSERT는 DB의 UNIQUE 제약에 걸려 `DataIntegrityViolationException`이 나고, 전역 예외 처리에서 409 `COMMON4091`로 바뀐다.
</details>
<details><summary>Q3. 없는 평점과 남의 평점을 왜 404와 403으로 나누고, 왜 404를 먼저 확인할까?</summary>

없는 것과 권한이 없는 것은 클라이언트가 해야 할 행동이 다르다. 주인을 비교하려면 먼저 평점을 찾아야 하므로, 찾기(404) → 주인 비교(403) 순서가 자연스럽다.
</details>
<details><summary>Q4. PATCH에서 <code>ratingRepository.save()</code>를 안 불러도 DB가 바뀌는 이유는?</summary>

`@Transactional` 안에서 조회한 엔티티는 JPA가 관리한다. 값을 바꾸면 트랜잭션이 끝날 때 처음 상태와 비교해서(변경 감지) UPDATE를 자동으로 보낸다.
</details>
