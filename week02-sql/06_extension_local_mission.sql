-- 확장 미션. 자신의 1주차 ERD(로컬 맛집 미션 서비스)로 조회 요구사항 1개
-- 준비: week01-erd/schema.sql을 먼저 실행해 local_mission DB와 테이블을 만든다.
--
-- 요구사항: "서울 지역의 가게 목록을 리뷰 개수, 평균 평점과 함께 평점 높은 순으로 보여 주세요."
--
-- 기준 테이블: stores(s) — 목록의 한 줄 = 가게 하나.
-- JOIN 이유:
--   regions(r): 지역 "이름"으로 거르기 위해 FK(s.region_id = r.id)로 연결 (INNER JOIN).
--   reviews(rv): 리뷰 개수·평균을 내기 위해 연결. 리뷰가 0개인 가게도 목록에 나와야 하므로 LEFT JOIN.
-- WHERE: r.name = '서울'.
-- 집계: 가게별로 GROUP BY 후 COUNT(rv.id), AVG(rv.rating). COUNT(*)가 아니라 COUNT(rv.id)여야 리뷰 0개가 0으로 나온다.
-- 정렬: 평균 평점 DESC, 같으면 리뷰 많은 순.
--
-- JOIN 경로: stores --(region_id)--> regions
--            stores --(store_id)--> reviews (LEFT)
USE local_mission;

-- 확인용 더미 데이터
INSERT INTO regions (name) VALUES ('서울'), ('부산');
INSERT INTO food_categories (name) VALUES ('카페');
INSERT INTO users (nickname, email) VALUES ('욱', 'wook@umc.com'), ('지수', 'jisu@umc.com'), ('민서', 'minseo@umc.com');
INSERT INTO stores (region_id, food_category_id, name, address) VALUES
    (1, 1, '서울 카페 A', '서울시 마포구'),
    (1, 1, '서울 카페 B', '서울시 강남구'),
    (2, 1, '부산 카페 C', '부산시 해운대구'),
    (1, 1, '서울 카페 D', '서울시 종로구'); -- 리뷰 없는 가게
INSERT INTO reviews (user_id, store_id, rating, content) VALUES
    (1, 1, 5, '최고'), (2, 1, 5, '맛있어요'), (3, 1, 4, '좋아요'),
    (1, 2, 4, '괜찮아요'), (2, 2, 3, '보통'),
    (1, 3, 5, '바다 뷰');

SELECT s.id                     AS store_id,
       s.name                   AS store_name,
       r.name                   AS region_name,
       COUNT(rv.id)             AS review_count,
       ROUND(AVG(rv.rating), 1) AS avg_rating
FROM stores s
JOIN regions r ON s.region_id = r.id
LEFT JOIN reviews rv ON s.id = rv.store_id
WHERE r.name = '서울'
GROUP BY s.id, s.name, r.name
ORDER BY avg_rating DESC, review_count DESC;

-- 실행 결과
-- | store_id | store_name  | region_name | review_count | avg_rating |
-- | 1        | 서울 카페 A | 서울        | 3            | 4.7        |
-- | 2        | 서울 카페 B | 서울        | 2            | 3.5        |
-- | 4        | 서울 카페 D | 서울        | 0            | NULL       |
-- 검증: 부산 가게는 빠지고, 리뷰 없는 서울 카페 D도 0개로 포함되며, 평점 높은 순으로 정렬되어 요구사항과 일치한다.
-- (MySQL은 DESC 정렬에서 NULL을 맨 뒤에 둔다)
