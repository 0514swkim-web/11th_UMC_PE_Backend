# UMC Web Backend

UMC 웹 파트 워크북 백엔드 실습·미션 기록입니다.

## 기술 스택
- Java 21, Spring Boot, Gradle
- MySQL, Spring Data JPA
- Postman

## 폴더 구조
| 폴더 | 내용 |
|---|---|
| `week01-erd/` | 1주차 ERD 설계 |
| `week02-sql/` | 2주차 SQL 쿼리 |
| `book-rental/` | 3~4주차 도서 대여 API |
| `movie/` | 5주차부터 이어서 만드는 영화 평점 서비스 |
| `docs/weeks/` | 주차별 개념 정리 |
| `docs/captures/` | 주차별 Postman·실행 결과 캡처 |

## 진행 현황
| 주차 | 주제 | PR | 핵심 캡처 |
|---|---|---|---|
| 0 | 클라이언트와 서버, HTTP | | [POST 201](docs/captures/week00/03-post-created.png) · [404](docs/captures/week00/04-not-found.png) |
| 1 | 요구사항을 데이터로 바꾸기 (ERD) | | [ERD](week01-erd/erd.png) |
| 2 | SQL로 데이터 다루기 | | [미션 1](docs/captures/week02/02-mission-1.png) · [확장](docs/captures/week02/05-extension.png) |
| 3 | 첫 API 만들고 검증하기 | | [카테고리 조회](docs/captures/week03/04-mission-category.png) · [대여 DB](docs/captures/week03/06-mission-rental-db.png) |
| 4 | ORM | | [등록 201](docs/captures/week04/02-post-201.png) · [SQL 로그](docs/captures/week04/08-sql-log.png) |
| 5 | 화면과 API를 처음 연결하기 | | [CORS 에러](docs/captures/week05/01-cors-error.png) · [umcine 화면](docs/captures/week05/07-umcine-members.png) |
| 6 | 입력한 데이터를 저장하기 | | [회원가입 201](docs/captures/week06/04-signup-201.png) · [평점 등록 201](docs/captures/week06/08-rating-201.png) · [남의 평점 403](docs/captures/week06/14-rating-403.png) |
| 7 | 로그인한 사용자를 구분하기 (JWT) | | [로그인 200](docs/captures/week07/01-login-200.png) · [내 정보 200](docs/captures/week07/06-members-me-200.png) · [남의 평점 403](docs/captures/week07/16-rating-patch-other-403.png) |
| 7 | 로그인한 사용자를 구분하기 | | |
