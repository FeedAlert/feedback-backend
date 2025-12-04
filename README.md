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

### Profiles de Ambiente

O projeto suporta dois profiles principais, ambos usando Pub/Sub GCP:

#### **Development (dev)** - Padrão
- Conecta com Pub/Sub real do GCP (mesmo ambiente de produção)
- Porta do backend: `8080`
- Logging mais verboso (DEBUG)
- Configuração: `application-dev.properties`
- Requer variável de ambiente: `GCP_PROJECT_ID`

#### **Production (prod)**
- Conecta com Pub/Sub real do GCP
- Porta padrão do Spring Boot: `8080`
- Logging menos verboso (INFO/WARN)
- Configuração: `application-prod.properties`
- Requer variável de ambiente: `GCP_PROJECT_ID`

**Como usar:**
```bash
# Development (padrão)
export GCP_PROJECT_ID=seu-project-id
mvn spring-boot:run

# Ou explicitamente
export GCP_PROJECT_ID=seu-project-id
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Production
export SPRING_PROFILES_ACTIVE=prod
export GCP_PROJECT_ID=seu-project-id
mvn spring-boot:run
```

### Ambiente Local (Docker Compose)

#### 1. Iniciar Banco de Dados

```bash
cd feedback-backend
docker-compose up -d postgres
```

Isso irá iniciar:
- PostgreSQL na porta `5432`

**⚠️ Importante**: Tanto dev quanto prod usam Pub/Sub real do GCP. Certifique-se de ter:
- O tópico `feedback-events` criado no Pub/Sub
- A `feedback-notification-function` deployada no GCP
- A variável de ambiente `GCP_PROJECT_ID` configurada

#### 2. Executar o Backend

```bash
export GCP_PROJECT_ID=seu-project-id
cd feedback-backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

A aplicação estará disponível em: `http://localhost:8080`

### Ambiente Cloud SQL (Produção/Desenvolvimento)

⚠️ **IMPORTANTE**: O banco Cloud SQL está configurado para aceitar conexões apenas via Cloud SQL Proxy por segurança.

#### 1. Baixar Cloud SQL Proxy

**macOS (ARM64):**
```bash
curl -o cloud-sql-proxy https://storage.googleapis.com/cloud-sql-connectors/cloud-sql-proxy/v2.8.0/cloud-sql-proxy.darwin.arm64
chmod +x cloud-sql-proxy
```

**macOS (Intel):**
```bash
curl -o cloud-sql-proxy https://storage.googleapis.com/cloud-sql-connectors/cloud-sql-proxy/v2.8.0/cloud-sql-proxy.darwin.amd64
chmod +x cloud-sql-proxy
```

**Windows:**
```powershell
# Baixe de: https://github.com/GoogleCloudPlatform/cloud-sql-proxy/releases
# Ou via PowerShell:
Invoke-WebRequest -Uri "https://storage.googleapis.com/cloud-sql-connectors/cloud-sql-proxy/v2.8.0/cloud-sql-proxy.windows.amd64.exe" -OutFile "cloud-sql-proxy.exe"
```

Mais opções: https://cloud.google.com/sql/docs/mysql/sql-proxy?hl=pt-br#install

#### 2. Obter Credenciais da Service Account

O arquivo JSON da service account (`glossy-ally-476722-p5-46a7447b9399.json`) deve estar na raiz do projeto.

⚠️ **NUNCA commite este arquivo no Git!** Ele já está no `.gitignore`.

#### 3. Iniciar Cloud SQL Proxy

**Linux/macOS:**
```bash
./scripts/start-cloud-sql-proxy.sh
```

**Windows:**
```cmd
scripts\start-cloud-sql-proxy.bat
```

**Ou manualmente:**
```bash
./cloud-sql-proxy --credentials-file=./glossy-ally-476722-p5-46a7447b9399.json glossy-ally-476722-p5:us-central1:feedalert-db
```

⚠️ **Mantenha o terminal do proxy aberto** enquanto a aplicação estiver rodando.

#### 4. Configurar Variáveis de Ambiente

Crie um arquivo `.env.local` na raiz do projeto (ou exporte as variáveis):

```bash
export SPRING_DATASOURCE_URL='jdbc:postgresql://127.0.0.1:5432/feedalert_db'
export SPRING_DATASOURCE_USERNAME='postgres'
export SPRING_DATASOURCE_PASSWORD='Uq$(t16uI8=~VO8#'
```

**No Windows (PowerShell):**
```powershell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://127.0.0.1:5432/feedalert_db"
$env:SPRING_DATASOURCE_USERNAME="postgres"
$env:SPRING_DATASOURCE_PASSWORD="Uq$(t16uI8=~VO8#"
```

#### 5. Executar a Aplicação

```bash
mvn spring-boot:run
```

A aplicação se conectará ao Cloud SQL através do proxy em `127.0.0.1:5432`.

### Verificar Saúde da Aplicação

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

Quando um feedback é marcado como **urgente** (`isUrgent: true`), o sistema publica automaticamente um evento no tópico `feedback-events` do Pub/Sub do GCP, que é processado pela função serverless de notificação deployada no GCP.

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

### Migrações (Flyway)

O projeto utiliza **Flyway** para gerenciar migrações do banco de dados. As migrations estão em `src/main/resources/db/migration/`:

- `V1__Create_Schema.sql` - Criação das tabelas
- `V2__Insert_initial_data.sql` - Dados iniciais (seed)

As migrações são executadas automaticamente na inicialização da aplicação.

### Estrutura do Banco

O banco de dados possui as seguintes tabelas:

- `tb_role` - Roles de usuários (STUDENT, ADMIN)
- `tb_user` - Usuários do sistema
- `tb_course` - Cursos disponíveis
- `tb_feedback` - Feedbacks dos estudantes

### Dados Iniciais

O script de inicialização (`scripts/init-db.sql`) cria:
- Roles: `STUDENT` e `ADMIN`
- Usuário admin padrão: `admin@example.com`
- 3 cursos de exemplo

## 🧪 Configuração de Ambiente

### Variáveis de Ambiente

Configure as variáveis no `application.properties` ou via environment variables:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/feedback_db
spring.datasource.username=feedback_user
spring.datasource.password=feedback_password

# Google Cloud Pub/Sub (obrigatório para dev e prod)
GCP_PROJECT_ID=seu-project-id
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

