# 0주차 BE: 클라이언트와 서버, 그리고 HTTP

> 한 줄 요약: **클라이언트가 HTTP로 "요청"하면 서버가 "응답"한다.** 요청에는 Method와 URL, 응답에는 Status Code와 데이터(JSON)가 담긴다.

## 1. 핵심 개념

| 개념 | 쉬운 설명 | 비유 |
|---|---|---|
| **클라이언트** | 요청하는 쪽 (브라우저, 앱, Postman) | 식당 손님 |
| **서버** | 요청을 받아 처리하고 응답하는 쪽 | 주방 |
| **프론트엔드 / 백엔드** | 화면을 만드는 쪽 / 데이터와 규칙을 처리하는 쪽 | 홀 / 주방 |
| **API** | 프로그램끼리 주고받는 약속 (무엇을, 어떻게 요청하면, 무엇이 오는지) | 메뉴판 + 주문 규칙 |
| **URL** | 자원의 주소. `프로토콜://호스트:포트/경로?쿼리` | 도로명 주소 |
| **Path Variable** | 경로 안의 값으로 자원을 콕 집기 (`/posts/1`) | 몇 호실 |
| **Query Parameter** | `?key=value`로 조건 붙이기 (`?postId=1`) | 주문 옵션 |
| **HTTP** | 클라이언트와 서버가 대화하는 규칙 | 공용 언어 |
| **Method** | 무엇을 할지: GET 조회, POST 생성, PUT/PATCH 수정, DELETE 삭제 | 주문 / 추가 / 변경 / 취소 |
| **Status Code** | 결과 요약: 2xx 성공, 4xx 클라이언트 잘못, 5xx 서버 잘못 | 주문 결과 안내 |
| **JSON** | `{ "key": value }` 형태의 데이터 형식 | 표준 주문서 양식 |
| **Postman** | 화면 없이 API를 직접 호출해 보는 도구 | 주방에 직접 주문하는 전화기 |

### 자주 보는 Status Code
| 코드 | 뜻 | 언제 |
|---|---|---|
| 200 OK | 성공 | 조회·수정 성공 |
| 201 Created | 생성됨 | POST로 새 데이터 생성 |
| 204 No Content | 성공, 보낼 내용 없음 | 삭제 성공 |
| 400 Bad Request | 요청 형식이 틀림 | 필수값 누락, 타입 불일치 |
| 401 / 403 | 인증 안 됨 / 권한 없음 | 로그인 안 함 / 남의 글 수정 |
| 404 Not Found | 자원이 없음 | 없는 ID 조회 |
| 500 Internal Server Error | 서버 내부 오류 | 서버 코드 버그 |

## 2. 이번 주 실습

워크북 6장 Postman 실습 4개를 컬렉션 파일로 정리했다: [jsonplaceholder.postman_collection.json](../../week00-postman/jsonplaceholder.postman_collection.json)
(Postman → **Import** → 파일 선택 → 요청 4개가 생김)

| 실습 | 요청 | 기대 결과 |
|---|---|---|
| 1 | `GET /posts/1` | 200 OK, 1번 게시글 JSON |
| 2 | `GET /comments?postId=1` | 200 OK, 댓글 배열 |
| 3 | `POST /posts` + JSON body | 201 Created, `id: 101` 포함 응답 |
| 4 | `GET /posts/99999` | 404 Not Found, `{}` |

> 0주차는 별도의 필수 제출 미션이 없다.

## 3. 자주 하는 실수
- POST에 body를 넣고 `Content-Type: application/json`을 빼먹기 → 서버가 JSON으로 못 읽는다.
- 404를 "서버가 고장났다"로 오해 → 서버는 정상이고, 그 자원이 없다는 대답이다.
- Path Variable과 Query Parameter 혼동 → "무엇을"은 경로, "어떤 조건으로"는 쿼리.

## 4. 스스로 점검하기
<details><summary>Q1. 브라우저 주소창으로 POST를 보낼 수 없는 이유는?</summary>

주소창은 GET 요청만 보낸다. body를 담은 POST는 폼, JS(fetch), Postman 같은 도구로 보낸다.
</details>
<details><summary>Q2. 4xx와 5xx의 차이는?</summary>

4xx는 요청한 쪽(클라이언트)의 문제, 5xx는 서버 쪽 문제.
</details>
