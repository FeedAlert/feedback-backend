-- V2__Insert_initial_data

-- Seed roles
INSERT INTO tb_role (name, description) VALUES
    ('STUDENT', 'Estudante - pode criar feedbacks sobre cursos'),
    ('ADMIN', 'Administrador - pode visualizar todos os feedbacks e relatórios')
ON CONFLICT (name) DO NOTHING;

-- Seed default admin user
INSERT INTO tb_user (name, email, role_id)
SELECT 'Administrador', 'admin@example.com', role_id
FROM tb_role WHERE name = 'ADMIN'
ON CONFLICT (email) DO NOTHING;

-- Seed default courses
INSERT INTO tb_course (title, description) VALUES
    ('Introdução à Arquitetura de Software', 'Curso básico sobre princípios de arquitetura de software'),
    ('Domain-Driven Design', 'Aprofundamento em DDD e padrões de design'),
    ('Clean Architecture', 'Implementação de arquitetura limpa em projetos Java')
ON CONFLICT DO NOTHING;
