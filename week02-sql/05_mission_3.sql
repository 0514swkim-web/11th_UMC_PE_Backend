-- 미션 3. 특정 책(book_id = 1)의 태그 목록과 특정 사용자(user_id = 1)의 좋아요 여부 조회
--
-- 기준 테이블: book(b) — 상세 화면의 주인공인 책 한 권에서 시작한다.
-- JOIN 이유:
--   book_tag(bt) → tag(t): 책과 태그는 N:M이라 중간 테이블 book_tag를 거쳐 태그 이름을 가져온다.
--   book_like(bl): 이 사용자의 좋아요 기록. 좋아요를 안 했어도 책은 나와야 하므로 LEFT JOIN,
--                  "이 사용자"의 기록만 붙이도록 bl.user_id = 1 조건을 ON 절에 둔다.
--   태그가 없는 책도 조회되도록 태그 쪽도 LEFT JOIN.
-- WHERE: book_id = 1인 책 한 권.
-- 좋아요 여부: bl.user_id가 NULL이면 기록 없음 → '안 함'.
--
-- JOIN 경로: book --(book_id)--> book_tag --(tag_id)--> tag
--            book --(book_id, user_id = 1)--> book_like
USE umc_study;

SELECT b.title,
       t.name AS tag_name,
       CASE WHEN bl.user_id IS NOT NULL THEN '좋아요' ELSE '안 함' END AS liked_by_user
FROM book b
LEFT JOIN book_tag bt ON b.book_id = bt.book_id
LEFT JOIN tag t ON bt.tag_id = t.tag_id
LEFT JOIN book_like bl ON b.book_id = bl.book_id AND bl.user_id = 1
WHERE b.book_id = 1
ORDER BY t.tag_id;

-- 실행 결과
-- | title       | tag_name | liked_by_user |
-- | 달빛 도서관 | 소설     | 좋아요        |
-- | 달빛 도서관 | 추천     | 좋아요        |
-- 검증: 1번 책의 태그 2개(소설, 추천)와 민서의 좋아요 여부(좋아요)가 함께 조회되어 요구사항과 일치한다.
