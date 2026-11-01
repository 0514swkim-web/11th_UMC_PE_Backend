# 4주차 BE: ORM으로 생산성 높이고 첫 API 완성하기 (Spring Data JPA)

> 한 줄 요약: **테이블을 엔티티 클래스로, SQL 문자열을 Repository 메서드로 바꾼다.** 요청·응답은 DTO로 분리해서 DB 구조가 곧 API 약속이 되지 않게 한다.

## 1. 핵심 개념

| 개념 | 쉬운 설명 | 비유 |
|---|---|---|
| **ORM** | 객체(클래스)와 테이블을 이어 주고 SQL을 대신 만들어 줌 | 통역사 |
| **JPA / Hibernate** | JPA는 자바 ORM 표준 명세, Hibernate는 그 구현체 | 레시피 표준서 / 요리사 |
| **Entity (`@Entity`)** | 테이블 한 개에 대응하는 클래스. `@Id`, `@Column(name = "snake_case")` | 테이블의 자바 버전 |
| **`@ManyToOne` + `@JoinColumn`** | N:1 관계. FK 컬럼을 객체 참조로 (`book.getCategory()`) | "소속 반" 칸에 반 객체를 연결 |
| **`FetchType.LAZY`** | 연관 객체는 실제로 쓸 때 조회 | 필요할 때만 창고에서 꺼내기 |
| **N+1 문제** | 목록 1번 + 항목마다 연관 조회 N번 → 쿼리 폭증. `@EntityGraph`(fetch join)로 한 번에 | 택배를 한 번에 안 받고 하나씩 받기 |
| **JpaRepository** | `findById`, `save` 등 기본 CRUD 제공 + 메서드 이름으로 쿼리 생성 | 기본 기능 다 있는 창고 관리 앱 |
| **쿼리 메서드** | `findAllByTitleContainingOrderByBookIdDesc` → `WHERE title LIKE ? ORDER BY book_id DESC` | 이름이 곧 주문서 |
| **영속성 컨텍스트 / 변경 감지** | 트랜잭션 안에서 엔티티 필드만 바꾸면 커밋 때 UPDATE 자동 실행 | 메모장에 적어 두고 퇴근 때 한 번에 반영 |
| **`@Transactional`** | 여러 DB 작업을 하나로 묶음. 실패하면 전부 되돌림 | 계약서 전체 서명 |
| **DTO (record)** | 요청·응답 전용 객체. Entity를 그대로 내보내지 않음 | 손님용 메뉴판 (주방 재고표 X) |
| **Bean Validation** | `@NotNull`, `@NotBlank`, `@Size` + `@Valid` → 잘못된 요청은 입구에서 400 | 입구 보안 검색대 |
| **`@RestControllerAdvice`** | 예외를 상태 코드·메시지로 바꾸는 전역 처리기 | 고객센터 안내 데스크 |
| **`ddl-auto: validate`** | 테이블은 그대로 두고 엔티티와 구조가 맞는지만 검사 | 설계도와 건물 대조 |

## 2. 이번 주 실습 → 파일 위치 (`book-rental/`)

| 워크북 단원 | 실습 | 파일 |
|---|---|---|
| 환경 | JPA·Validation 의존성, `ddl-auto: validate`, `show-sql` | [build.gradle](../../book-rental/build.gradle), [application.yml](../../book-rental/src/main/resources/application.yml) |
| 2 | ERD → 엔티티 | [Book](../../book-rental/src/main/java/com/umc/study/entity/Book.java), [Category](../../book-rental/src/main/java/com/umc/study/entity/Category.java) |
| 3 | JpaRepository | [BookRepository](../../book-rental/src/main/java/com/umc/study/repository/BookRepository.java), [CategoryRepository](../../book-rental/src/main/java/com/umc/study/repository/CategoryRepository.java) |
| 4 | 요청·응답 DTO | [CreateBookRequest](../../book-rental/src/main/java/com/umc/study/dto/CreateBookRequest.java), [BookResponse](../../book-rental/src/main/java/com/umc/study/dto/BookResponse.java) |
| 5 | `GET /books` 최신순 | [BookService.getBooks](../../book-rental/src/main/java/com/umc/study/service/BookService.java) |
| 6 | `POST /books` 201, 없는 카테고리 404 | `BookService.createBook`, [GlobalExceptionHandler](../../book-rental/src/main/java/com/umc/study/exception/GlobalExceptionHandler.java) |

### 필수 미션 키워드 → 구현
| 키워드 | 구현 |
|---|---|
| `Book`, `Category` 엔티티와 다대일 관계 | `Book.category` — `@ManyToOne(fetch = LAZY)` + `@JoinColumn(name = "category_id")` |
| `GET /books`: ID·제목·설명·카테고리 이름·대여 가능 여부, 최신순 | `findAllByOrderByBookIdDesc` + `BookResponse` |
| `POST /books`: 201 | `@ResponseStatus(HttpStatus.CREATED)` |
| DTO 검증 | `@NotNull categoryId`, `@NotBlank @Size(max = 100) title` + `@Valid` → 400 `{"message": ...}` |
| 존재하지 않는 카테고리 예외 | `NotFoundException` → 404 `{"message": "존재하지 않는 카테고리입니다."}` |
| 3주차 API 전체를 ORM으로 | 카테고리별 조회 `findAllByCategory_CategoryIdOrderByBookIdDesc`, 대여·반납 [Rental](../../book-rental/src/main/java/com/umc/study/entity/Rental.java) / [RentalService](../../book-rental/src/main/java/com/umc/study/service/RentalService.java) |

### 선택 미션
| 선택 | 구현 |
|---|---|
| 1. categoryName 포함, Entity 대신 Response DTO | `BookResponse.categoryName` (+ `@EntityGraph`로 N+1 방지) |
| 2. 제목 검색 `GET /books?keyword=` | `findAllByTitleContainingOrderByBookIdDesc` |

### 3주차 Raw SQL과 비교해 바뀐 점
1. 3주차에는 `INSERT INTO book (...) VALUES (?, ?, ?)`처럼 SQL 문자열과 `?` 순서를 직접 관리했지만, 4주차에는 엔티티를 한 번 정의하고 `findById`, `save`, 쿼리 메서드로 표현했다.
2. 3주차 응답은 `book_id`, `category_id: 1`, `is_available: true`처럼 DB 컬럼이 그대로 나갔지만, 4주차는 `BookResponse`로 `bookId`, `categoryName: "문학"`처럼 API용 이름으로 응답한다.
3. 3주차에는 빈 제목도 저장되고 없는 카테고리는 FK 오류로 500이었지만, 4주차는 DTO 검증으로 400, 없는 카테고리는 404를 원인 메시지와 함께 준다.
4. 대여 반납은 `UPDATE` SQL 대신 `rental.markReturned()`로 필드만 바꾸면 커밋 때 JPA가 UPDATE를 실행한다(변경 감지).
5. 그래도 `show-sql` 로그를 보면 `JOIN ... ORDER BY book_id DESC`가 그대로 나간다. ORM을 써도 테이블 관계와 SQL 이해는 필요하다.

검증: H2 테스트 9개(조회 DTO, 201, 빈 제목 400, 없는 카테고리 404, 카테고리별, 검색, 대여 7일, 없는 도서 404, 반납) 모두 통과 — [BookRentalApiTest](../../book-rental/src/test/java/com/umc/study/BookRentalApiTest.java).

## 3. 자주 하는 실수
- Entity를 그대로 응답 → LAZY 연관 객체 때문에 직렬화 오류가 나거나 DB 구조가 노출된다.
- `@Valid`를 빼먹기 → DTO에 붙인 검증이 동작하지 않는다.
- 예외 처리기 없이 `IllegalArgumentException` 던지기 → 500으로 응답된다.
- `open-in-view: false`인데 트랜잭션 밖에서 LAZY 필드 접근 → `LazyInitializationException`. DTO 변환은 Service(트랜잭션 안)에서.
- `ddl-auto: create`로 실행 → 기존 테이블과 데이터가 지워진다. 이미 있는 테이블은 `validate`.

## 4. 스스로 점검하기
<details><summary>Q1. <code>@EntityGraph(attributePaths = "category")</code>가 없으면 무슨 일이?</summary>

목록 조회 1번 후 `getCategory().getName()`을 부를 때마다 category를 따로 조회해서 N+1 쿼리가 나간다.
</details>
<details><summary>Q2. 반납 API에 <code>save()</code> 호출이 없는데 어떻게 DB가 바뀌나?</summary>

`@Transactional` 안에서 조회한 엔티티는 영속성 컨텍스트가 관리한다. 필드를 바꾸면 커밋 시점에 변경을 감지해 UPDATE를 실행한다.
</details>
