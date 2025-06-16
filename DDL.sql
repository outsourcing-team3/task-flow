DROP DATABASE IF EXISTS taskFlow;
CREATE DATABASE IF NOT EXISTS taskFlow;
USE taskFlow;

-- 인증 정보(auth) 테이블
CREATE TABLE auth (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '사용자 ID',
    name VARCHAR(50) NOT NULL COMMENT '사용자 이름',
    username VARCHAR(20) NOT NULL COMMENT '로그인 아이디',
    email VARCHAR(100) NOT NULL COMMENT '이메일',
    password VARCHAR(255) NOT NULL COMMENT '패스워드',
    role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '권한',

-- BaseEntity 공통 필드들
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '삭제 여부',
    created_at DATETIME NOT NULL COMMENT '생성일',
    updated_at DATETIME NOT NULL COMMENT '수정일',
    deleted_at DATETIME COMMENT '삭제일',

    PRIMARY KEY (id),
    UNIQUE KEY uk_auth_username (username),
    UNIQUE KEY uk_auth_email (email),

    INDEX idx_auth_email (email),
    INDEX idx_auth_username (username),
    INDEX idx_auth_is_deleted (is_deleted)
) COMMENT = '인증 정보 Table';

-- 사용자 프로필(users) 테이블
CREATE TABLE users (
    id BIGINT NOT NULL COMMENT '사용자 ID (Auth와 동일한 PK)',
    name VARCHAR(50) NOT NULL COMMENT '이름',
    email VARCHAR(100) NOT NULL COMMENT '이메일 (캐시용)',

-- BaseEntity 공통 필드들
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '삭제 여부',
    created_at DATETIME NOT NULL COMMENT '생성일',
    updated_at DATETIME NOT NULL COMMENT '수정일',
    deleted_at DATETIME COMMENT '삭제일',

    PRIMARY KEY (id),
-- 이벤트 기반이므로 FK 제약조건 제거 (데이터 일관성은 애플리케이션에서 보장)
    UNIQUE KEY uk_users_email (email),

    INDEX idx_users_is_deleted (is_deleted),
    INDEX idx_users_email (email)
) COMMENT = '사용자 프로필 Table';

CREATE TABLE refresh_tokens (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '리프레시 토큰 ID',
    token VARCHAR(500) NOT NULL COMMENT '토큰 값',
    user_id BIGINT NOT NULL COMMENT '유저 ID (FK)',
    expiry_time DATETIME NOT NULL COMMENT '만료 시간',

-- BaseEntity 공통 필드들
    created_at DATETIME NOT NULL COMMENT '생성일',
    updated_at DATETIME NOT NULL COMMENT '수정일',

    PRIMARY KEY (id),
    UNIQUE KEY uk_refresh_tokens_token (token),
    INDEX idx_refresh_tokens_user_id (user_id),
    INDEX idx_refresh_tokens_expiry_time (expiry_time),

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE token_blacklist (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '토큰 블랙리스트 ID',
    jti VARCHAR(36) NOT NULL COMMENT 'JWT ID (JTI)',
    user_id BIGINT NOT NULL COMMENT '유저 ID (FK)',
    expiry_time DATETIME NOT NULL COMMENT '만료 시간',

-- BaseEntity 공통 필드들 (soft delete, updated_at 불필요)
    created_at DATETIME NOT NULL COMMENT '생성일',

    PRIMARY KEY (id),
    UNIQUE KEY uk_token_blacklist_jti (jti),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,

    INDEX idx_token_blacklist_user_id (user_id),
    INDEX idx_token_blacklist_expiry_time (expiry_time)
);

-- 태스크(tasks) 테이블 생성
CREATE TABLE tasks
(
    id          BIGINT AUTO_INCREMENT COMMENT '태스크 ID (PK)',
    title       VARCHAR(255) COMMENT '태스크 제목',
    description LONGTEXT COMMENT '태스크 내용',
    priority    VARCHAR(10) COMMENT '태스크 우선순위',
    assignee_id BIGINT COMMENT '태스크 담당자 (FK)',
    creator_id  BIGINT COMMENT '태스크 생성자 (FK)',
    deadline    DATETIME COMMENT '태스크 마감일',
    status      VARCHAR(20) COMMENT '태스크 상태',
    started_at  DATETIME NOT NULL COMMENT '태스크 시작일',

    is_deleted  BOOLEAN  NOT NULL DEFAULT FALSE COMMENT '삭제 여부',
    created_at  DATETIME NOT NULL COMMENT '생성일',
    updated_at  DATETIME NOT NULL COMMENT '수정일',
    deleted_at  DATETIME COMMENT '삭제일',

    PRIMARY KEY (id),
    FOREIGN KEY (assignee_id) REFERENCES users (id),
    FOREIGN KEY (creator_id) REFERENCES users (id)
) COMMENT = '태스크 Table';

-- 댓글(comments) 테이블 생성
CREATE TABLE comments (
    id BIGINT AUTO_INCREMENT COMMENT '댓글의 고유 ID',
    user_id BIGINT NOT NULL COMMENT '댓글 작성자 (유저 ID)',
    content VARCHAR(255) NOT NULL COMMENT '댓글 내용',
    task_id BIGINT NOT NULL COMMENT '댓글이 달린 태스크의 식별자',

    is_deleted  BOOLEAN  NOT NULL DEFAULT FALSE COMMENT '삭제 여부',
    created_at  DATETIME NOT NULL COMMENT '생성일',
    updated_at  DATETIME NOT NULL COMMENT '수정일',
    deleted_at  DATETIME COMMENT '삭제일',

    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (task_id) REFERENCES tasks(id),

    INDEX idx_comments_task_id (task_id),
    INDEX idx_comments_user_id (user_id)
);

-- 활동 타입(activity_type) 테이블 생성
CREATE TABLE activity_types (
    id BIGINT AUTO_INCREMENT COMMENT '활동 타입 ID (PK)',
    code VARCHAR(100) NOT NULL UNIQUE COMMENT '타입 코드',
    label VARCHAR(50) NOT NULL COMMENT '라벨',

    PRIMARY KEY (id)
) COMMENT = '활동 타입 Table';

-- 활동 로그(activity_logs) 테이블 생성
CREATE TABLE activity_logs (
    id BIGINT AUTO_INCREMENT COMMENT '활동 로그 ID (PK)',
    activity_time TIMESTAMP NOT NULL COMMENT '활동 시간',
    user_id BIGINT NULL COMMENT '유저 ID (FK)',
    target_id BIGINT NULL COMMENT '작업 ID',
    activity_type_id BIGINT NULL COMMENT '활동 타입 ID (FK)',
    changes TEXT NULL COMMENT '변경 사항',

    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (activity_type_id) REFERENCES activity_types(id) ON DELETE SET NULL,

    INDEX idx_logs_user_id (user_id),
    INDEX idx_logs_task_id (target_id),
    INDEX idx_logs_activity_type_id (activity_type_id)
) COMMENT = '활동 로그 Table';