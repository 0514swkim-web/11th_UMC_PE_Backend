-- 5주차 영화 평점 서비스 스키마 (Member 1:N Rating, Member 1:N Favorite)
-- movie_id는 TMDB의 영화 ID를 그대로 담는 일반 컬럼 (우리 DB에 movie 테이블이 없으므로 FK 아님)
CREATE DATABASE IF NOT EXISTS umc_movie;
USE umc_movie;

CREATE TABLE member (
    member_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    email       VARCHAR(254) NOT NULL UNIQUE,  -- RFC 5321 이메일 최대 길이
    password    VARCHAR(60)  NOT NULL,         -- bcrypt 해시는 항상 60자 (6주차)
    nickname    VARCHAR(50)  NOT NULL UNIQUE,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE rating (
    rating_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id   BIGINT   NOT NULL,
    movie_id    BIGINT   NOT NULL,
    score       TINYINT  NOT NULL,
    comment     TEXT,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member (member_id),
    UNIQUE KEY uk_rating_member_movie (member_id, movie_id)
);

-- 8주차 즐겨찾기 실습에서 사용할 테이블. 이번 주차에는 스키마만 만들어 둔다.
CREATE TABLE favorite (
    favorite_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id   BIGINT   NOT NULL,
    movie_id    BIGINT   NOT NULL,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member (member_id),
    UNIQUE KEY uk_favorite_member_movie (member_id, movie_id)
);
