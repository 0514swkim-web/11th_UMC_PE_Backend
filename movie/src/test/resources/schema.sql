-- 테스트용: movie/sql/schema.sql과 같은 스키마

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
