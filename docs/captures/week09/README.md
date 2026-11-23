# 9주차 캡처 체크리스트

워크북 넣을 곳: **미션 기록**(필수 미션) = Swagger 문서 화면 + 테스트 전체 통과.

Swagger 주소: `http://localhost:8080/swagger-ui/index.html` (문서 JSON은 `/v3/api-docs`)

| 파일 이름 | 무엇을 찍나 | 노션 칸 | 상태 |
|---|---|---|---|
| `01-swagger-list.png` | Swagger 전체 목록. 토큰이 필요한 API에만 자물쇠, 로그인·회원가입·중복 체크는 자물쇠 없음 | BE 사진01 | ✅ |
| `02a-swagger-post-ratings.png` | `POST /ratings` 펼침. 한 줄 설명, Request body 예시, 201 `COMMON201` | BE 사진02 | ✅ |
| `02b-swagger-post-ratings-401.png` | 같은 API의 401 (`AUTH401_2`/`3`/`4`) 예시 | BE 사진02 | ✅ |
| `02c-swagger-post-ratings-409.png` | 같은 API의 409 `RATING409_1` 예시 | BE 사진02 | ✅ |
| `03a-swagger-members-me.png` | `GET /members/me` 펼침. `memberId` 입력칸 없음(`@Parameter(hidden = true)`), 200 응답 | BE 사진03 | ✅ |
| `03b-swagger-members-me-401.png` | 같은 API의 401 예시 | BE 사진03 | ✅ |
| `03c-swagger-delete-favorite.png` | `DELETE /favorites/{movieId}` 펼침. 입력칸은 `movieId` 하나 | BE 사진03 | ✅ |
| `03d-swagger-delete-favorite-404.png` | 같은 API의 404 `FAVORITE404_1` 예시 | BE 사진03 | ✅ |
| `04-test-pass.png` | IntelliJ `src/test` 전체 실행 → 51개 테스트 통과, BUILD SUCCESSFUL | BE 사진04 | ✅ |

## 미리보기

### Swagger 전체 목록
![](01-swagger-list.png)

### 평점 등록 `POST /ratings`
![](02a-swagger-post-ratings.png)
![](02b-swagger-post-ratings-401.png)
![](02c-swagger-post-ratings-409.png)

### 내 정보 조회 `GET /members/me`
![](03a-swagger-members-me.png)
![](03b-swagger-members-me-401.png)

### 즐겨찾기 해제 `DELETE /favorites/{movieId}`
![](03c-swagger-delete-favorite.png)
![](03d-swagger-delete-favorite-404.png)

### 테스트 51개 통과
![](04-test-pass.png)
