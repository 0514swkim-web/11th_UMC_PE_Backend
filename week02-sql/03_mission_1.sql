-- 미션 1. 문학 카테고리의 대여 가능한 도서를 최신순으로 10개 조회
-- 결과: 책 제목, 설명, 카테고리 이름
--
-- 기준 테이블: book(b) — 제목·설명·대여 가능 여부가 book에 있다.
-- JOIN 이유: 카테고리 "이름"은 category에 있으므로 FK(b.category_id = c.category_id)로 연결한다.
-- WHERE: 카테고리 이름이 '문학'이고 is_available이 TRUE인 책만 남긴다.
-- 정렬·범위: AUTO_INCREMENT인 book_id가 클수록 최근 등록이므로 DESC, 최대 10개(LIMIT 10).
--
-- JOIN 경로: book --(category_id)--> category
USE umc_study;

SELECT b.book_id, b.title, b.description, c.name AS category_name
FROM book b
JOIN category c ON b.category_id = c.category_id
WHERE c.name = '문학'
  AND b.is_available = TRUE
ORDER BY b.book_id DESC
LIMIT 10;

-- 실행 결과
-- | book_id | title       | description | category_name |
-- | 1       | 달빛 도서관 | 소설        | 문학          |
-- 검증: 문학 카테고리 책 2권 중 대여 가능한 '달빛 도서관' 1권만 조회되어 요구사항과 일치한다.
