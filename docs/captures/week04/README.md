# 4주차 캡처 체크리스트

준비: 3주차와 같은 DB(`umc_study`)와 환경 변수로 `StudyApplication` 실행 → Postman에서 `book-rental/postman/week04.postman_collection.json` Import.

| 파일 이름 | 무엇을 찍나 |
|---|---|
| `01-get-books.png` | `GET /books` → 200, 최신순 + `bookId`, `categoryName`, `isAvailable` |
| `02-post-201.png` | `POST /books` → **201 Created** + 응답 DTO |
| `03-post-400.png` | 빈 제목 → **400** `{"message": "title은 비어 있을 수 없습니다."}` |
| `04-post-404.png` | 없는 카테고리 → **404** `{"message": "존재하지 않는 카테고리입니다."}` |
| `05-category.png` | `GET /books/category/1` → 문학 책만 |
| `06-keyword.png` | (선택) `GET /books?keyword=우주` |
| `07-rental.png` | `POST /rentals` → 201, `dueAt`이 `rentedAt` 7일 뒤 |
| `08-sql-log.png` | IntelliJ 콘솔에서 `show-sql`로 찍힌 `select ... join category ... order by book_id desc` |

> 실행 시 `Schema-validation: wrong column type` 오류가 나면 `application.yml`의 `ddl-auto`를 `none`으로 바꾸고 알려 주세요 (MySQL 버전별 타입 차이).
