-- 테스트용: movie/sql/seed.sql과 같은 더미 데이터

INSERT INTO member (email, password, nickname) VALUES
    ('noco@umc.com', '$2a$10$dummyEncodedPasswordValue', '수현'),
    ('minseo@umc.com', '$2a$10$dummyEncodedPasswordValue', '민서');

-- 550: Fight Club, 155: The Dark Knight (TMDB 영화 ID)
INSERT INTO rating (member_id, movie_id, score, comment) VALUES
    (1, 550, 5, '최고의 영화!'),
    (1, 155, 4, '다시 봐도 명작'),
    (2, 550, 3, '호불호가 갈릴 것 같아요');
