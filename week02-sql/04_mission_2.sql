-- 미션 2. 특정 사용자(user_id = 1, 민서)가 아직 반납하지 않은 책을 반납 예정일 순으로 조회
-- 결과: 책 제목, 대여일, 반납 예정일
--
-- 기준 테이블: rental(r) — "누가 언제 무엇을 빌렸는지"가 rental에 있다.
-- JOIN 이유: 책 제목은 book에 있으므로 FK(r.book_id = b.book_id)로 연결한다.
-- WHERE: user_id = 1이고 returned_at IS NULL(미반납)인 기록만. NULL은 = 가 아니라 IS NULL로 비교한다.
-- 정렬: due_at ASC — 반납 예정일이 가까운 책부터.
--
-- JOIN 경로: rental --(book_id)--> book
USE umc_study;

SELECT b.title, r.rented_at, r.due_at
FROM rental r
JOIN book b ON r.book_id = b.book_id
WHERE r.user_id = 1
  AND r.returned_at IS NULL
ORDER BY r.due_at ASC;

-- 실행 결과
-- | title       | rented_at           | due_at              |
-- | 겨울의 편지 | 2026-08-10 10:00:00 | 2026-08-17 10:00:00 |
-- 검증: 민서의 미반납 대여 기록이 '겨울의 편지' 1건으로 조회되어 요구사항과 일치한다.
