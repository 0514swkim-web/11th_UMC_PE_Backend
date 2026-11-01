# 5주차 캡처 체크리스트

준비: [5주차 요약본의 실행 방법](../../weeks/week05.md#실행-방법) → Postman에서 `movie/postman/week05.postman_collection.json` Import.

| 파일 이름 | 무엇을 찍나 | 상태 |
|---|---|---|
| `01-cors-error.png` | 실습 0. `mock-frontend`(5173)에서 버튼 클릭 → Console의 **CORS policy** 에러 (WebConfig 주석 처리 상태) | ✅ |
| `02-nickname.png` | `GET /members/nickname/수현` → `{"available": false}` | ✅ |
| `03-email.png` | `GET /members/email/noco@umc.com` → `{"available": false}` | ✅ |
| `04-ratings.png` | `GET /members/1/ratings` → 평점 2개 최신순 | ✅ |
| `05-ratings-404.png` | `GET /members/999/ratings` → 404 | ✅ |
| `06-cors-ok-mock.png` | WebConfig 적용 후 `mock-frontend` 버튼 → 화면에 `{"available":false}` | ✅ |
| `07-umcine-members.png` | **필수 미션**: FE `umcine`의 `http://localhost:5173/members` 화면에서 세 API 호출 결과 | ✅ |
| `08-umcine-network.png` | 같은 화면의 Network 탭 → 응답 헤더 `Access-Control-Allow-Origin: http://localhost:5173` | ✅ |

## 미리보기

### 실습 0. CORS 설정 전: 브라우저가 응답을 막음
![](01-cors-error.png)

### 실습 1. 닉네임 중복 체크
![](02-nickname.png)

### 실습 2. 이메일 중복 체크
![](03-email.png)

### 실습 3. 유저별 평점 (최신순)
![](04-ratings.png)

### 실습 3. 없는 유저 평점 (404)
![](05-ratings-404.png)

### CORS 설정 후: 간이 프론트 정상 응답
![](06-cors-ok-mock.png)

### 필수 미션. umcine 회원 API 화면
![](07-umcine-members.png)

### 필수 미션. 응답 헤더의 허용 출처
![](08-umcine-network.png)
