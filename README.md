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

### Autenticação

#### Login
```bash
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@example.com",
  "password": "admin123"
}
```

**Resposta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 6,
  "email": "admin@example.com",
  "role": "ADMIN"
}
```

**Nota:** Use o token retornado no header `Authorization: Bearer {token}` para acessar endpoints protegidos.

### Feedbacks

#### Criar Feedback
```bash
POST /api/feedbacks
Content-Type: application/json
Authorization: Bearer {token}

{
  "courseId": 1,
  "rating": 5,
  "comment": "Excelente curso!",
  "isUrgent": false
}
```

**Validações:**
- `courseId`: obrigatório (Long)
- `rating`: obrigatório, entre 1 e 5 (Integer)
- `comment`: opcional (String)
- `isUrgent`: opcional, padrão false (Boolean)

**Resposta (201 Created):**
```json
{
  "feedbackId": 1,
  "course": {
    "courseId": 1,
    "title": "Introdução à Arquitetura de Software",
    "description": "..."
  },
  "student": {
    "userId": 6,
    "name": "Administrador",
    "email": "admin@example.com"
  },
  "rating": 5,
  "comment": "Excelente curso!",
  "isUrgent": false,
  "createdAt": "2025-01-09T10:30:00Z"
}
```

#### Listar Feedbacks (Apenas ADMIN)
```bash
GET /api/feedbacks
Authorization: Bearer {token}
```

**Filtros disponíveis:**
```bash
GET /api/feedbacks?courseId=1
GET /api/feedbacks?userId=1
GET /api/feedbacks?urgent=true
GET /api/feedbacks?urgent=false
```

**Nota:** Apenas usuários com role `ADMIN` podem listar todos os feedbacks. Estudantes só podem ver seus próprios feedbacks.

#### Buscar Feedback por ID
```bash
GET /api/feedbacks/{id}
Authorization: Bearer {token}
```

**Regras de acesso:**
- `ADMIN`: pode ver qualquer feedback
- `STUDENT`: pode ver apenas seus próprios feedbacks
- Retorna 403 (Forbidden) se tentar acessar feedback de outro usuário

**Resposta (200 OK):**
```json
{
  "feedbackId": 1,
  "course": { ... },
  "student": { ... },
  "rating": 5,
  "comment": "Excelente curso!",
  "isUrgent": false,
  "createdAt": "2025-01-09T10:30:00Z"
}
```

### Cursos

#### Listar Cursos (Público)
```bash
GET /api/courses
```

**Resposta (200 OK):**
```json
[
  {
    "courseId": 1,
    "title": "Introdução à Arquitetura de Software",
    "description": "Curso básico sobre princípios de arquitetura de software"
  }
]
```

#### Buscar Curso por ID (Público)
```bash
GET /api/courses/{id}
```

**Resposta (200 OK):**
```json
{
  "courseId": 1,
  "title": "Introdução à Arquitetura de Software",
  "description": "Curso básico sobre princípios de arquitetura de software"
}
```

### Health Check

#### Verificar Saúde da Aplicação (Público)
```bash
GET /actuator/health
```

**Resposta (200 OK):**
```json
{
  "status": "UP"
}
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

## 🚀 Deploy no Google Cloud Run

### Pré-requisitos

1. **Google Cloud SDK (gcloud)** instalado e configurado
   ```bash
   # Instalar: https://cloud.google.com/sdk/docs/install
   gcloud auth login
   gcloud config set project glossy-ally-476722-p5
   ```

2. **Docker** instalado e rodando
   ```bash
   docker info  # Verificar se está rodando
   ```

3. **Permissões necessárias no GCP:**
   - Owner ou Editor do projeto
   - Permissões para Cloud Run, Cloud SQL, Secret Manager, Pub/Sub

### Configuração Inicial (Primeira Vez)

#### 1. Criar Service Account

```bash
# Criar service account
gcloud iam service-accounts create feed-alert-sa \
  --display-name="Feed Alert Service Account" \
  --project=glossy-ally-476722-p5

# Conceder permissões necessárias
gcloud projects add-iam-policy-binding glossy-ally-476722-p5 \
  --member="serviceAccount:feed-alert-sa@glossy-ally-476722-p5.iam.gserviceaccount.com" \
  --role="roles/cloudsql.client"

gcloud projects add-iam-policy-binding glossy-ally-476722-p5 \
  --member="serviceAccount:feed-alert-sa@glossy-ally-476722-p5.iam.gserviceaccount.com" \
  --role="roles/secretmanager.secretAccessor"

gcloud projects add-iam-policy-binding glossy-ally-476722-p5 \
  --member="serviceAccount:feed-alert-sa@glossy-ally-476722-p5.iam.gserviceaccount.com" \
  --role="roles/pubsub.publisher"
```

#### 2. Criar Secret do Banco de Dados

```bash
# Criar secret com a senha do banco
echo -n "sua-senha-do-banco" | gcloud secrets create db-password \
  --data-file=- \
  --replication-policy="automatic" \
  --project=glossy-ally-476722-p5
```

#### 3. Configurar Permissões do Banco de Dados

Conecte-se ao Cloud SQL e execute:

```sql
-- Conceder permissões ao usuário do banco
GRANT USAGE ON SCHEMA public TO feedback_user;
GRANT CREATE ON SCHEMA public TO feedback_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO feedback_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO feedback_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO feedback_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO feedback_user;
```

#### 4. Criar Tópico Pub/Sub

```bash
# Criar tópico para notificações
gcloud pubsub topics create feedback-notifier \
  --project=glossy-ally-476722-p5
```

### Deploy Automatizado

#### Opção 1: Usando Script de Deploy (Recomendado)

```bash
# Dar permissão de execução
chmod +x deploy.sh

# Executar deploy
./deploy.sh
```

O script irá:
1. ✅ Verificar autenticação no GCP
2. ✅ Fazer build da imagem Docker (linux/amd64)
3. ✅ Autenticar Docker no GCP
4. ✅ Enviar imagem para Container Registry
5. ✅ Verificar/criar secret do banco
6. ✅ Fazer deploy no Cloud Run
7. ✅ Testar health check

#### Opção 2: Deploy Manual via Console

1. **Build e Push da Imagem:**
   ```bash
   # Build
   docker build --platform linux/amd64 -t gcr.io/glossy-ally-476722-p5/feed-alert:latest .
   
   # Autenticar Docker
   gcloud auth configure-docker
   
   # Push
   docker push gcr.io/glossy-ally-476722-p5/feed-alert:latest
   ```

2. **Deploy via Console GCP:**
   - Acesse: https://console.cloud.google.com/run
   - Clique em "Create Service" ou edite o serviço existente
   - Configure:
     - **Container image:** `gcr.io/glossy-ally-476722-p5/feed-alert:latest`
     - **Service name:** `feed-alert`
     - **Region:** `us-central1`
     - **Authentication:** Allow unauthenticated invocations
     - **Service account:** `feed-alert-sa@glossy-ally-476722-p5.iam.gserviceaccount.com`
     - **Memory:** 512 MiB
     - **CPU:** 1
     - **Min instances:** 0
     - **Max instances:** 10
     - **Timeout:** 300s

3. **Configurar Variáveis de Ambiente:**
   - `SPRING_DATASOURCE_URL`: `jdbc:postgresql:///feedalert_db?cloudSqlInstance=glossy-ally-476722-p5:us-central1:feedalert-db&socketFactory=com.google.cloud.sql.postgres.SocketFactory`
   - `SPRING_DATASOURCE_USERNAME`: `feedback_user`
   - `SPRING_DATASOURCE_PASSWORD`: (usar Secret Manager: `db-password`)
   - `GCLOUD_PROJECT`: `glossy-ally-476722-p5`
   - `SPRING_PROFILES_ACTIVE`: `prod`
   - `APP_NOTIFICATION_MODE`: `pubsub`
   - `SPRING_FLYWAY_ENABLED`: `false`
   - `JWT_SECRET`: (gerar com `openssl rand -base64 32`)

4. **Configurar Cloud SQL Connection:**
   - Adicionar Cloud SQL instance: `glossy-ally-476722-p5:us-central1:feedalert-db`

5. **Deploy:**
   - Clique em "Create" ou "Deploy"

### Verificar Deploy

```bash
# Ver status do serviço
gcloud run services describe feed-alert --region us-central1

# Ver logs
gcloud run services logs read feed-alert --region us-central1 --limit 50

# Testar health check
curl https://feed-alert-552003690311.us-central1.run.app/actuator/health
```

### Atualizar Deploy

Para atualizar após mudanças no código:

```bash
# Simplesmente execute o script novamente
./deploy.sh
```

O script irá:
- Rebuild da imagem com as mudanças
- Push da nova imagem
- Deploy automático no Cloud Run

### Troubleshooting

#### Erro: Permission denied on secret
```bash
# Verificar se service account tem permissão
gcloud projects get-iam-policy glossy-ally-476722-p5 \
  --flatten="bindings[].members" \
  --filter="bindings.members:serviceAccount:feed-alert-sa@glossy-ally-476722-p5.iam.gserviceaccount.com"
```

#### Erro: PERMISSION_DENIED: Method doesn't allow unregistered callers (Pub/Sub)
```bash
# Conceder permissão de publisher
gcloud projects add-iam-policy-binding glossy-ally-476722-p5 \
  --member="serviceAccount:feed-alert-sa@glossy-ally-476722-p5.iam.gserviceaccount.com" \
  --role="roles/pubsub.publisher"
```

#### Erro: Connection refused (Cloud SQL)
- Verificar se Cloud SQL instance está configurada no Cloud Run
- Verificar se service account tem role `roles/cloudsql.client`

#### Ver logs detalhados
```bash
gcloud run services logs read feed-alert --region us-central1 --follow
```

## 🔒 Segurança

O sistema implementa:

- ✅ **Autenticação JWT** - Tokens JWT para autenticação
- ✅ **Autorização baseada em roles** - ADMIN e STUDENT com permissões diferentes
- ✅ **Isolamento de dados** - Estudantes só podem ver seus próprios feedbacks
- ✅ **Validação de entrada** - Validação de DTOs com Bean Validation
- ✅ **HTTPS obrigatório** - Cloud Run força HTTPS
- ✅ **Service Account** - Aplicação usa service account dedicada com permissões mínimas necessárias



## 📖 Como Usar a Collection do Postman

1. **Importar Collection:**
   - Abra o Postman ou Insomnia
   - Importe o arquivo `postman_collection.json`

2. **Configurar Variáveis:**
   - A collection já vem com a variável `base_url` configurada
   - Após fazer login, os tokens são salvos automaticamente nas variáveis `admin_token` e `student_token`

3. **Testar Endpoints:**
   - Execute primeiro "Login ADMIN" para obter o token
   - Os endpoints protegidos usarão automaticamente o token salvo
   - Para testar como estudante, execute "Login STUDENT"

4. **Nota:** A collection inclui scripts que salvam automaticamente os tokens após login, então você só precisa fazer login uma vez por sessão.

## 📄 Licença

Este projeto faz parte de um desafio técnico para demonstração de conhecimentos em Cloud Computing e Serverless.

