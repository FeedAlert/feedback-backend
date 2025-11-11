-- V1__Create_Schema

CREATE TABLE tb_role (
    role_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE tb_user (
    user_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    role_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    CONSTRAINT fk_role
        FOREIGN KEY(role_id)
        REFERENCES tb_role(role_id)
);

CREATE TABLE tb_course (
    course_id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE tb_feedback (
    feedback_id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    is_urgent BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    CONSTRAINT fk_course FOREIGN KEY(course_id) REFERENCES tb_course(course_id) ON DELETE CASCADE,
    CONSTRAINT fk_user FOREIGN KEY(user_id) REFERENCES tb_user(user_id)
);

CREATE INDEX idx_feedback_course_id ON tb_feedback(course_id);
CREATE INDEX idx_feedback_user_id ON tb_feedback(user_id);
CREATE INDEX idx_feedback_is_urgent ON tb_feedback(is_urgent);
CREATE INDEX idx_feedback_created_at ON tb_feedback(created_at);
CREATE INDEX idx_user_email ON tb_user(email);
CREATE INDEX idx_user_role_id ON tb_user(role_id);
