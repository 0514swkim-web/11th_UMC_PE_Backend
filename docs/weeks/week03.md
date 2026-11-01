# 3주차 BE: 첫 API 만들고 검증하기 (Spring + 생 SQL)

> 한 줄 요약: **Controller(웨이터) → Service(셰프) → Repository(창고지기) 3계층으로 요청을 처리한다.** 이번 주는 일부러 JdbcTemplate + 생 SQL + `Map`으로 만들어서, 4주차 ORM과 DTO가 왜 필요한지 몸으로 느낀다.

## 1. 핵심 개념

| 개념 | 쉬운 설명 | 비유 |
|---|---|---|
| **3계층 아키텍처** | 요청 받기 / 규칙 처리 / DB 접근을 나눔 | 웨이터 / 셰프 / 창고지기 |
| **`@RestController`** | 메서드 반환값을 JSON(또는 문자열)으로 응답하는 클래스 | 주문 받는 카운터 |
| **`@RequestMapping` / `@GetMapping` / `@PostMapping` / `@PatchMapping`** | URL·Method와 메서드를 연결 (라우팅) | 메뉴별 담당 웨이터 |
| **`@PathVariable`** | `/books/category/{categoryId}`의 값을 매개변수로 | 주소의 호수 읽기 |
| **`@RequestBody`** | 요청 JSON body를 객체(이번엔 `Map`)로 | 주문서 받아 읽기 |
| **`@Service` / `@Repository`** | 스프링이 관리하는 부품(Bean)으로 등록 | 직원 명부에 등록 |
| **의존성 주입 (DI)** | 필요한 부품을 직접 `new` 하지 않고 스프링이 생성자로 넣어 줌. `@RequiredArgsConstructor` + `final` | 회사가 장비 지급 |
| **커넥션 풀 (HikariCP)** | DB 연결을 미리 여러 개 만들어 돌려 씀 | 미리 깔아 둔 전화선 |
| **JdbcTemplate** | SQL을 실행하고 결과를 받아 주는 스프링 도구 | 창고 출입 카드 |
| **파라미터 바인딩 `?`** | 값을 SQL 문자열에 붙이지 않고 따로 전달 → **SQL Injection 방지** | 주문서 칸에만 쓰기 |
| **환경 변수** | `${DB_PW}`처럼 비밀번호를 코드 밖(IntelliJ 실행 구성)에 둠 | 금고 비밀번호는 머릿속에 |

## 2. 이번 주 실습 → 파일 위치 (`book-rental/`)

| 워크북 단원 | 실습 | 파일 |
|---|---|---|
| 세팅 | Spring Boot 3.5 / Java 21 / Web·JDBC·MySQL·Lombok | [build.gradle](../../book-rental/build.gradle), [application.yml](../../book-rental/src/main/resources/application.yml) |
| 3 | `GET /books` | [BookRepository](../../book-rental/src/main/java/com/umc/study/repository/BookRepository.java) → [BookService](../../book-rental/src/main/java/com/umc/study/service/BookService.java) → [BookController](../../book-rental/src/main/java/com/umc/study/controller/BookController.java) |
| 4 | `POST /books` (파라미터 바인딩) | `BookRepository.save` |
| 미션 1 | `GET /books/category/{categoryId}` | `BookRepository.findByCategoryId` (`WHERE category_id = ?`) |
| 미션 2 | `POST /rentals` | [RentalRepository](../../book-rental/src/main/java/com/umc/study/repository/RentalRepository.java) (`NOW()`, `DATE_ADD(NOW(), INTERVAL 7 DAY)`), [RentalController](../../book-rental/src/main/java/com/umc/study/controller/RentalController.java) |
| 선택 미션 | `PATCH /rentals/{rentalId}/return` | `RentalRepository.markReturned` (`UPDATE ... SET returned_at = NOW()`), 없는 ID는 404 |
| 검증 | H2 메모리 DB 요청 테스트 | [BookRentalApiTest](../../book-rental/src/test/java/com/umc/study/BookRentalApiTest.java) |
| Postman | 요청 7개 컬렉션 | [week03.postman_collection.json](../../book-rental/postman/week03.postman_collection.json) |

### 실행 방법
1. MySQL에서 `week02-sql/01_schema.sql`, `02_seed.sql` 실행 (DB `umc_study`)
2. IntelliJ로 `book-rental` 폴더 열기 → 실행 구성 → 환경 변수
   `DB_URL=jdbc:mysql://localhost:3306/umc_study;DB_USER=root;DB_PW=본인비밀번호`
3. `StudyApplication` 실행 → Postman에서 컬렉션 Import 후 순서대로 Send

```bash
cd book-rental
./gradlew test    # MySQL 없이 H2로 API 테스트 (DATE_ADD 테스트 1개는 H2 미지원이라 건너뜀)
```

## 3. 직접 겪어 보는 "생 SQL + No DTO"의 불편함 (→ 4주차 동기)
- `body.get("titel")`처럼 오타를 내도 **컴파일러가 못 잡는다.** 실행해 봐야 `null`이 들어간 걸 안다.
- 응답 JSON 키가 DB 컬럼명(`category_id`, `is_available`)과 똑같다 → DB를 바꾸면 프론트가 깨진다.
- SQL이 문자열이라 오타는 실행 시점에야 `BadSqlGrammarException`.

## 4. 자주 하는 실수
- `"... WHERE title = '" + title + "'"`처럼 문자열 이어 붙이기 → SQL Injection.
- 환경 변수를 등록하지 않고 실행 → `Could not resolve placeholder 'DB_URL'`.
- `@RequestBody` 없이 `Map`을 받으면 body가 비어 있다.
- POST 요청에 Body 타입을 JSON으로 안 바꾸고 보내기 → 415 Unsupported Media Type.

## 5. 스스로 점검하기
<details><summary>Q1. Service가 지금은 Repository 호출만 하는데 왜 두나?</summary>

나중에 "대여 가능한 책인지 확인", "없는 카테고리면 오류" 같은 비즈니스 규칙이 들어갈 자리다. Controller와 Repository를 깔끔하게 유지한다.
</details>
<details><summary>Q2. 반납 API에서 없는 rentalId를 어떻게 404로 구분했나?</summary>

`jdbcTemplate.update`가 돌려주는 "바뀐 행 수"가 0이면 그런 기록이 없다는 뜻이라 404를 응답했다.
</details>
