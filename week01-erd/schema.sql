-- 1주차 미션: 로컬 맛집 미션 서비스 ERD (erd.png)를 DDL로 옮긴 것
-- 컨벤션: snake_case, PK는 BIGINT AUTO_INCREMENT, 회원은 Soft Delete(is_deleted)

CREATE DATABASE IF NOT EXISTS local_mission;
USE local_mission;

CREATE TABLE regions (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    name       VARCHAR(100) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE food_categories (
    id         BIGINT      NOT NULL AUTO_INCREMENT,
    name       VARCHAR(50) NOT NULL,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE users (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    nickname          VARCHAR(50)  NOT NULL,
    email             VARCHAR(255) NOT NULL,
    phone_number      VARCHAR(20)  NULL,
    profile_image_url TEXT         NULL,
    bio               TEXT         NULL,
    is_deleted        BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE (email)
);

CREATE TABLE stores (
    id               BIGINT        NOT NULL AUTO_INCREMENT,
    region_id        BIGINT        NOT NULL,
    food_category_id BIGINT        NOT NULL,
    name             VARCHAR(100)  NOT NULL,
    address          VARCHAR(255)  NOT NULL,
    phone            VARCHAR(20)   NULL,
    latitude         DECIMAL(10,8) NULL,
    longitude        DECIMAL(11,8) NULL,
    image_url        TEXT          NULL,
    rating           DECIMAL(3,2)  NOT NULL DEFAULT 0.00,
    created_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (region_id) REFERENCES regions (id),
    FOREIGN KEY (food_category_id) REFERENCES food_categories (id)
);

CREATE TABLE missions (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    store_id      BIGINT       NOT NULL,
    title         VARCHAR(100) NOT NULL,
    description   TEXT         NULL,
    reward_points INT          NOT NULL,
    start_date    DATE         NOT NULL,
    end_date      DATE         NOT NULL,
    mission_type  VARCHAR(50)  NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (store_id) REFERENCES stores (id)
);

-- 회원이 도전한 미션 (users N:M missions 의 중간 테이블)
CREATE TABLE member_missions (
    id             BIGINT      NOT NULL AUTO_INCREMENT,
    user_id        BIGINT      NOT NULL,
    mission_id     BIGINT      NOT NULL,
    status         VARCHAR(50) NOT NULL, -- IN_PROGRESS / COMPLETED
    completed_date TIMESTAMP   NULL,
    created_at     TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (mission_id) REFERENCES missions (id)
);

CREATE TABLE reviews (
    id         BIGINT    NOT NULL AUTO_INCREMENT,
    user_id    BIGINT    NOT NULL,
    store_id   BIGINT    NOT NULL,
    rating     INT       NOT NULL,
    content    TEXT      NULL,
    image_url  TEXT      NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (store_id) REFERENCES stores (id)
);

-- 포인트 적립·사용 내역 (balance: 이 내역 반영 후 잔액)
CREATE TABLE points (
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    user_id     BIGINT      NOT NULL,
    amount      INT         NOT NULL,
    type        VARCHAR(50) NOT NULL, -- EARN / USE
    description TEXT        NULL,
    balance     INT         NOT NULL,
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);
