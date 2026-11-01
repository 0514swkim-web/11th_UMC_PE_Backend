# 2주차 BE: SQL로 데이터 다루기

> 한 줄 요약: **요구사항 문장을 "결과 → 테이블 → 관계(JOIN) → 조건(WHERE) → 정렬·범위" 순서로 SQL로 바꾼다.** 다른 테이블의 정보가 필요할 때만 ERD의 FK를 따라 JOIN한다.

## 1. 핵심 개념

| 개념 | 쉬운 설명 | 비유 |
|---|---|---|
| **DDL / DML** | 구조를 만드는 SQL(`CREATE`, `ALTER`) / 데이터를 다루는 SQL(`SELECT`, `INSERT`, `UPDATE`, `DELETE`) | 서랍장 만들기 / 서랍에 물건 넣고 빼기 |
| **SELECT ... FROM** | 어떤 컬럼을 어느 테이블에서 꺼낼지 | 창고에서 물건 꺼내기 |
| **WHERE** | 행을 거르는 조건 | 체 |
| **NULL 비교** | `= NULL`은 항상 실패. `IS NULL` / `IS NOT NULL` | "모름"은 "모름"과도 같다고 할 수 없다 |
| **(INNER) JOIN** | 양쪽 모두에 짝이 있는 행만 합침 | 짝이 있는 사람만 입장 |
| **LEFT JOIN** | 왼쪽 행은 전부 남기고, 짝이 없으면 오른쪽은 NULL | 짝이 없어도 혼자 입장 가능 |
| **ON vs WHERE** | LEFT JOIN에서 오른쪽 조건을 WHERE에 쓰면 NULL 행이 사라져 INNER JOIN처럼 된다 → **ON에 쓴다** | 입장 조건 vs 퇴장 조건 |
| **ORDER BY / LIMIT / OFFSET** | 정렬 / 최대 개수 / 건너뛸 개수 (페이지네이션) | 줄 세우기 / 몇 명만 / 몇 명 건너뛰고 |
| **GROUP BY + 집계** | 묶어서 `COUNT`, `AVG`, `SUM` | 반별 평균 점수 |
| **SQL Injection** | 입력값을 문자열로 이어 붙이면 악성 SQL이 실행됨 → 파라미터 바인딩(`?`) | 주문서에 "주방 문 닫아" 끼워 넣기 |

## 2. 이번 주 실습 → 파일 위치

| 파일 | 내용 |
|---|---|
| [01_schema.sql](../../week02-sql/01_schema.sql) | 도서 대여 시스템 테이블 8개 (DB `umc_study`) |
| [02_seed.sql](../../week02-sql/02_seed.sql) | 공통 더미 데이터 |
| [03_mission_1.sql](../../week02-sql/03_mission_1.sql) | 미션 1. 문학·대여 가능 도서 최신순 10개 (`JOIN category`) |
| [04_mission_2.sql](../../week02-sql/04_mission_2.sql) | 미션 2. 민서의 미반납 도서, 반납 예정일 순 (`IS NULL`) |
| [05_mission_3.sql](../../week02-sql/05_mission_3.sql) | 미션 3. 1번 책 태그 + 좋아요 여부 (`LEFT JOIN`, 조건은 `ON`) |
| [06_extension_local_mission.sql](../../week02-sql/06_extension_local_mission.sql) | 확장. 1주차 ERD로 서울 가게별 리뷰 수·평균 평점 (`GROUP BY`) |

각 파일 주석에 **기준 테이블 / JOIN 이유 / WHERE / 정렬** 설명, JOIN 경로, 실행 결과, 한 줄 검증을 적었다. 모든 쿼리는 H2(MySQL 모드)로 실행해 주석의 결과와 같은지 확인했다.

### 예전 미션 기록에서 고친 점
| 항목 | 예전 | 수정 |
|---|---|---|
| 확장 쿼리 컬럼 이름 | `s.store_id`, `s.store_name`, `r.region_name` | 1주차 ERD에 맞춰 `s.id`, `s.name`, `r.name` |
| 확장 쿼리 결과 표 | `WHERE 서울`인데 부산 가게가 포함 | 서울 가게만 + 리뷰 0개 가게도 포함되는 결과 |
| 좋아요 여부 | MySQL 전용 `IF()` | 표준 SQL `CASE WHEN` (결과 동일) |

## 3. 자주 하는 실수
- `returned_at = NULL` → 결과 0건. `IS NULL`.
- LEFT JOIN 후 `WHERE bl.user_id = 1` → 좋아요 안 한 책이 사라진다. 조건을 `ON`으로.
- `COUNT(*)`로 리뷰 수 세기 → LEFT JOIN의 NULL 행도 1로 센다. `COUNT(rv.id)`.
- `GROUP BY`에 없는 컬럼을 SELECT → MySQL 설정에 따라 오류(`ONLY_FULL_GROUP_BY`).
- ORDER BY 없이 LIMIT → 매번 다른 행이 나올 수 있다.

## 4. 스스로 점검하기
<details><summary>Q1. 미션 3에서 태그 쪽도 LEFT JOIN을 쓴 이유는?</summary>

태그가 하나도 없는 책도 상세 화면에는 나와야 하기 때문. INNER JOIN이면 책 자체가 결과에서 빠진다.
</details>
<details><summary>Q2. 두 번째 페이지(한 페이지 10개)를 가져오는 범위는?</summary>

`LIMIT 10 OFFSET 10` (10개 건너뛰고 10개).
</details>
