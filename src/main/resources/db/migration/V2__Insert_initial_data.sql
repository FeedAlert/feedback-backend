-- V2__Insert_initial_data

-- Seed roles
INSERT INTO tb_role (name, description) 
SELECT * FROM (VALUES
    ('STUDENT', 'Estudante - pode criar feedbacks sobre cursos'),
    ('ADMIN', 'Administrador - pode visualizar todos os feedbacks e relatórios')
) AS v(name, description)
WHERE NOT EXISTS (
    SELECT 1 FROM tb_role WHERE tb_role.name = v.name
);

-- Seed default admin user
INSERT INTO tb_user (name, email, role_id)
SELECT 'Administrador', 'admin@example.com', role_id
FROM tb_role 
WHERE name = 'ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM tb_user WHERE tb_user.email = 'admin@example.com'
  );

-- Seed default courses
INSERT INTO tb_course (title, description) 
SELECT * FROM (VALUES
    ('Introdução à Arquitetura de Software', 'Curso básico sobre princípios de arquitetura de software'),
    ('Domain-Driven Design', 'Aprofundamento em DDD e padrões de design'),
    ('Clean Architecture', 'Implementação de arquitetura limpa em projetos Java')
) AS v(title, description)
WHERE NOT EXISTS (
    SELECT 1 FROM tb_course WHERE tb_course.title = v.title
);
