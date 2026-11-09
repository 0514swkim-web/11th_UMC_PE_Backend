# 7주차 캡처 체크리스트

워크북 넣을 곳: **실습 인증** = 로그인 성공·내 정보 조회·토큰으로 평점 등록, **미션 기록** = Postman 요청/응답, **트러블 슈팅** = 에러가 났을 때.

Postman 컬렉션: [`movie/postman/week07.postman_collection.json`](../../../movie/postman/week07.postman_collection.json) (시나리오 1~11)

| 파일 이름 | 무엇을 찍나 | 워크북 위치 | 상태 |
|---|---|---|---|
| `01-login-200.png` | 3단계. `POST /auth/login` (umc@example.com) → 200 `COMMON200`, `nickname`과 `accessToken` 발급 | 실습 인증 ① | ✅ |
| `02-login-401-wrong-password.png` | 3단계. 비밀번호 틀림 → 401 `AUTH401_1`, "이메일 또는 비밀번호가 올바르지 않습니다." | 미션 기록 · 트러블 슈팅 2 | ✅ |
| `03-login-401-no-email.png` | 3단계. 없는 이메일 → 02와 **똑같은** 401 `AUTH401_1` (가입 여부를 숨김) | 미션 기록 | ✅ |
| `04-no-token-401.png` | 4단계. 토큰 없이 잠긴 경로 → 401 `AUTH401_2`, "로그인이 필요한 요청입니다." (6주차엔 404였던 요청) | 미션 기록 | ✅ |
| `05-fake-token-401.png` | 4단계. 가짜 토큰 `abc` → 401 `AUTH401_3`, "유효하지 않은 토큰입니다." | 미션 기록 | ✅ |
| `06-members-me-200.png` | 5단계. `GET /members/me` + Bearer 토큰 → 200, 이메일·닉네임·가입일 (password 없음) | 실습 인증 ② | ✅ |
| `07-members-me-patch-200.png` | 6단계. `PATCH /members/me` 닉네임 `영화광` → 200 "회원 정보를 수정했습니다." | 미션 기록 | ✅ |
| `08-members-me-patch-409.png` | 6단계. 다른 회원이 쓰는 닉네임 `수현` → 409 `MEMBER409_1` | 미션 기록 | ✅ |
| `09-scenario-signup-a-201.png` | 7단계 시나리오 1. 회원 A 가입 → 201 (memberId 5) | 미션 기록 | ✅ |
| `10-scenario-signup-b-201.png` | 7단계 시나리오 2. 회원 B 가입 → 201 (memberId 6) | 미션 기록 | ✅ |
| `11-scenario-login-a-200.png` | 7단계 시나리오 3. A 로그인 → 토큰 A 저장 | 미션 기록 | ✅ |
| `12-scenario-login-b-200.png` | 7단계 시나리오 4. B 로그인 → 토큰 B 저장 | 미션 기록 | ✅ |
| `13-rating-create-201.png` | 7단계 시나리오 5. 토큰 A로 `POST /ratings` → 201 `COMMON201`, ratingId 5 | 실습 인증 ③ | ✅ |
| `14-rating-duplicate-409.png` | 7단계 시나리오 6. 같은 영화 다시 등록 → 409 `RATING409_1` | 미션 기록 | ✅ |
| `15-rating-patch-own-200.png` | 7단계 시나리오 7. 토큰 A로 `PATCH /ratings/5` → 200, score 3 | 미션 기록 | ✅ |
| `16-rating-patch-other-403.png` | 7단계 시나리오 8. **토큰 B**로 A의 평점 수정 → 403 `RATING403_1` (토큰은 멀쩡해도 인가 실패) | 미션 기록 | ✅ |
| `17-rating-no-token-401.png` | 7단계 시나리오 9. 토큰 없이 수정 → 401 `AUTH401_2` (인증 실패) | 미션 기록 | ✅ |
| `18-rating-delete-404.png` | 7단계 시나리오 10. `DELETE /ratings/99999` → 404 `RATING404_1` | 미션 기록 | ✅ |
| `19-rating-delete-own-200.png` | 7단계 시나리오 11. 토큰 A로 `DELETE /ratings/5` → 200 "평점을 삭제했습니다." | 미션 기록 | ✅ |
| `90-troubleshoot-postman-url.png` | 3단계 트러블슈팅. 주소 칸에 `POST http://...`까지 적어 `Invalid protocol: post http:` | 트러블 슈팅 1 | ✅ |

## 미리보기

### 3단계. login-200
![](01-login-200.png)

### 3단계. login-401-wrong-password
![](02-login-401-wrong-password.png)

### 3단계. login-401-no-email
![](03-login-401-no-email.png)

### 4단계. no-token-401
![](04-no-token-401.png)

### 4단계. fake-token-401
![](05-fake-token-401.png)

### 5단계. members-me-200
![](06-members-me-200.png)

### 6단계. members-me-patch-200
![](07-members-me-patch-200.png)

### 6단계. members-me-patch-409
![](08-members-me-patch-409.png)

### 7단계 시나리오 1. scenario-signup-a-201
![](09-scenario-signup-a-201.png)

### 7단계 시나리오 2. scenario-signup-b-201
![](10-scenario-signup-b-201.png)

### 7단계 시나리오 3. scenario-login-a-200
![](11-scenario-login-a-200.png)

### 7단계 시나리오 4. scenario-login-b-200
![](12-scenario-login-b-200.png)

### 7단계 시나리오 5. rating-create-201
![](13-rating-create-201.png)

### 7단계 시나리오 6. rating-duplicate-409
![](14-rating-duplicate-409.png)

### 7단계 시나리오 7. rating-patch-own-200
![](15-rating-patch-own-200.png)

### 7단계 시나리오 8. rating-patch-other-403
![](16-rating-patch-other-403.png)

### 7단계 시나리오 9. rating-no-token-401
![](17-rating-no-token-401.png)

### 7단계 시나리오 10. rating-delete-404
![](18-rating-delete-404.png)

### 7단계 시나리오 11. rating-delete-own-200
![](19-rating-delete-own-200.png)

### 3단계 트러블슈팅. troubleshoot-postman-url
![](90-troubleshoot-postman-url.png)
