# Sistema de Feedback para Cursos Online

Sistema backend desenvolvido seguindo **Clean Architecture**, **Domain-Driven Design (DDD)** e **Clean Code** para gerenciar feedbacks de estudantes sobre cursos online.

## 🏗️ Arquitetura

O projeto segue uma arquitetura em camadas seguindo os princípios de DDD:

```
├── domain/              # Camada de Domínio (Core Business Logic)
│   ├── model/          # Entidades e Value Objects
│   ├── service/        # Domain Services
│   ├── gateway/        # Interfaces (Dependency Inversion)
│   └── exception/      # Exceções de Domínio
│
├── application/         # Camada de Aplicação
│   ├── dto/            # DTOs de entrada/saída
│   ├── mapper/         # Mappers (MapStruct)
│   └── usecase/        # Casos de Uso (Orquestração)
│
├── infrastructure/      # Camada de Infraestrutura
│   ├── persistence/    # JPA Entities, Repositories
│   └── pubsub/         # Adapter Pub/Sub
│
└── presentation/        # Camada de Apresentação
    └── controller/     # REST Controllers
```

## 🚀 Tecnologias

- **Java 21**
- **Spring Boot 3.5.7**
- **PostgreSQL 16**
- **Google Cloud Pub/Sub**
- **MapStruct** (Mapeamento DTO ↔ Domain)
- **Lombok**
- **Docker & Docker Compose**

## 📋 Pré-requisitos

- Java 21+
- Maven 3.8+
- Docker & Docker Compose
- (Opcional) Google Cloud SDK para Pub/Sub em produção

## 🔧 Configuração e Execução

### 1. Iniciar Banco de Dados e Pub/Sub Emulator

```bash
docker-compose up -d
```

Isso irá iniciar:
- PostgreSQL na porta `5432`
- Pub/Sub Emulator na porta `8085`

### 2. Executar a Aplicação

```bash
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

### 3. Verificar Saúde da Aplicação

```bash
curl http://localhost:8080/actuator/health
```

## 📚 API Endpoints

### Feedbacks

#### Criar Feedback
```bash
POST /api/feedbacks
Content-Type: application/json
X-User-Id: 1

{
  "courseId": 1,
  "rating": 5,
  "comment": "Excelente curso!",
  "isUrgent": false
}
```

#### Listar Feedbacks
```bash
GET /api/feedbacks
GET /api/feedbacks?courseId=1
GET /api/feedbacks?userId=1
GET /api/feedbacks?urgent=true
```

#### Buscar Feedback por ID
```bash
GET /api/feedbacks/{id}
```

### Cursos

#### Listar Cursos
```bash
GET /api/courses
```

#### Buscar Curso por ID
```bash
GET /api/courses/{id}
```

## 🔔 Integração com Pub/Sub

Quando um feedback é marcado como **urgente** (`isUrgent: true`), o sistema publica automaticamente um evento no tópico `feedback-events` do Pub/Sub, que é processado pela função serverless de notificação (já implementada).

O formato do evento segue o especificado na documentação:

```json
{
  "rating": 1,
  "comment": "Erro crítico no curso",
  "isUrgent": true,
  "createdAt": "2025-01-15T10:30:00Z",
  "student": {
    "userId": "1",
    "name": "João Silva",
    "email": "joao@example.com"
  },
  "course": {
    "courseId": "1",
    "title": "Arquitetura de Software"
  }
}
```

## 🗄️ Banco de Dados

O banco de dados é criado automaticamente via Docker Compose com as seguintes tabelas:

- `tb_role` - Roles de usuários (STUDENT, ADMIN)
- `tb_user` - Usuários do sistema
- `tb_course` - Cursos disponíveis
- `tb_feedback` - Feedbacks dos estudantes

### Dados Iniciais

O script de inicialização (`scripts/init-db.sql`) cria:
- Roles: `STUDENT` e `ADMIN`
- Usuário admin padrão: `admin@example.com`
- 3 cursos de exemplo

## 🧪 Desenvolvimento Local

### Variáveis de Ambiente

Configure as variáveis no `application.properties` ou via environment variables:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/feedback_db
spring.datasource.username=feedback_user
spring.datasource.password=feedback_password

# Pub/Sub (para desenvolvimento local)
spring.cloud.gcp.pubsub.project-id=local-project
spring.cloud.gcp.pubsub.emulator-host=localhost:8085
```

### Build

```bash
mvn clean package
```

## 📝 Estrutura do Projeto

```
src/main/java/com/example/feedAlert/
├── domain/
│   ├── model/              # Entidades de domínio
│   ├── gateway/            # Interfaces dos repositórios
│   ├── service/            # Services de domínio
│   └── exception/          # Exceções de domínio
├── application/
│   ├── dto/                # DTOs
│   ├── mapper/             # MapStruct mappers
│   └── usecase/            # Casos de uso
├── infrastructure/
│   ├── persistence/        # JPA implementation
│   │   ├── entity/         # JPA entities
│   │   ├── jpa/            # JPA repositories
│   │   ├── mapper/         # Entity ↔ Domain mappers
│   │   └── repository/     # Repository implementations
│   └── pubsub/             # Pub/Sub adapter
├── presentation/
│   └── controller/         # REST controllers
└── config/                 # Configurações Spring
```

## 🔒 Segurança

⚠️ **Atenção**: A configuração de segurança atual permite acesso público para facilitar desenvolvimento. Em produção, implementar:

- Autenticação JWT
- Autorização baseada em roles
- Validação adequada de tokens
- HTTPS obrigatório

## 📦 Próximos Passos

- [ ] Implementar autenticação JWT completa
- [ ] Adicionar testes unitários e de integração
- [ ] Implementar relatórios semanais (serverless)
- [ ] Dashboard administrativo
- [ ] Documentação OpenAPI/Swagger

## 📄 Licença

Este projeto faz parte de um desafio técnico para demonstração de conhecimentos em Cloud Computing e Serverless.

