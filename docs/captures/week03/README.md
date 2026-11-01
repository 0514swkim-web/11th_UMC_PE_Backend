# 3주차 캡처 체크리스트

준비: [3주차 요약본의 실행 방법](../../weeks/week03.md#실행-방법) → Postman에서 `book-rental/postman/week03.postman_collection.json` Import.
**요청(URL·Body) + 상태 코드 + 응답**이 한 화면에 보이게 찍어 주세요.

| 파일 이름 | 무엇을 찍나 |
|---|---|
| `01-get-books.png` | 실습 1. `GET /books` → 200, 책 3권 |
| `02-post-book.png` | 실습 2. `POST /books` → Body + 200 "도서 등록이 완료되었습니다!" |
| `03-get-books-after.png` | 실습 2. 다시 `GET /books` → 맨 끝에 "클린 코드" |
| `04-mission-category.png` | 미션 1. `GET /books/category/1` → 문학 책만 |
| `05-mission-rental.png` | 미션 2. `POST /rentals` → 200 "대여 기록이 생성되었습니다!" |
| `06-mission-rental-db.png` | 미션 2. Workbench에서 `SELECT * FROM rental;` → 새 행의 `due_at`이 7일 뒤 |
| `07-return.png` | (선택) `PATCH /rentals/1/return` → 200 |
| `08-return-404.png` | (선택) `PATCH /rentals/999/return` → 404 |
| `09-intellij-run.png` | IntelliJ 콘솔 `Started StudyApplication` 로그 |
| `10-gradle-test.png` | (선택) `./gradlew test` BUILD SUCCESSFUL |
