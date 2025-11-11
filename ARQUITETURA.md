# Arquitetura do Sistema - Feedback Backend

## 📐 Visão Geral da Arquitetura

Este projeto implementa uma **Clean Architecture** combinada com princípios de **Domain-Driven Design (DDD)**, seguindo as melhores práticas de **Clean Code**. A arquitetura é dividida em camadas bem definidas, com dependências unidirecionais e separação clara de responsabilidades.

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                        │
│              (Controllers, DTOs, Exception Handlers)         │
└───────────────────────┬─────────────────────────────────────┘
                        │ depends on
┌───────────────────────▼─────────────────────────────────────┐
│                    Application Layer                         │
│            (Use Cases, Application Services, Mappers)        │
└───────────────────────┬─────────────────────────────────────┘
                        │ depends on
┌───────────────────────▼─────────────────────────────────────┐
│                      Domain Layer                            │
│        (Entities, Value Objects, Domain Services,            │
│         Repository Interfaces, Domain Exceptions)            │
└───────────────────────┬─────────────────────────────────────┘
                        │ implemented by
┌───────────────────────▼─────────────────────────────────────┐
│                  Infrastructure Layer                        │
│     (JPA Entities, Repository Implementations,               │
│      External Service Adapters, Configuration)               │
└─────────────────────────────────────────────────────────────┘
```

## 🏗️ Estrutura de Camadas

### 1. Domain Layer (Camada de Domínio)

**Responsabilidade**: Contém a lógica de negócio pura, sem dependências de frameworks ou infraestrutura.

#### 📁 `domain/model/`
- **Entidades de Domínio**: `Feedback`, `Course`, `User`, `Role`
- **Value Objects**: `Email`, `Name`, `Rating` (encapsulados em records)
- Características:
  - São objetos ricos com comportamento
  - Contêm validações de negócio
  - Independentes de persistência

#### 📁 `domain/service/`
- **Domain Services**: `FeedbackDomainService`
- Contém lógica de domínio que não pertence naturalmente a uma entidade
- Validações complexas e regras de negócio

#### 📁 `domain/gateway/`
- **Interfaces** (contratos) para repositórios e serviços externos
- Implementa o **Dependency Inversion Principle**
- Exemplos: `FeedbackRepository`, `CourseRepository`, `UserRepository`, `PubSubGateway`
- **Não há implementações aqui**, apenas interfaces

#### 📁 `domain/exception/`
- Exceções específicas do domínio
- `InvalidRatingException`, `DomainException`

**Regra de Ouro**: O Domain Layer **nunca** depende de outras camadas.

---

### 2. Application Layer (Camada de Aplicação)

**Responsabilidade**: Orquestra casos de uso, coordena fluxos de trabalho e transforma dados entre camadas.

#### 📁 `application/dto/`
- **Data Transfer Objects** para comunicação externa
- `CreateFeedbackRequest`: DTO de entrada
- `FeedbackResponse`: DTO de saída
- `CourseResponse`: DTO de resposta de cursos
- Utilizam `@Valid` para validações de entrada

#### 📁 `application/mapper/`
- **MapStruct Mappers** para conversão entre DTOs e Domain Models
- `FeedbackMapper`: Converte `Feedback` ↔ `FeedbackResponse`
- `CourseMapper`: Converte `Course` ↔ `CourseResponse`
- Geração de código em tempo de compilação (performance)

#### 📁 `application/usecase/`
- **Use Cases** (Casos de Uso) que implementam fluxos de negócio
- `CreateFeedbackUseCase`: Cria feedback e publica evento se urgente
- `GetFeedbackUseCase`: Busca feedbacks com diferentes filtros
- `GetCourseUseCase`: Busca cursos
- Características:
  - Orquestram múltiplos repositórios
  - Coordenam validações e regras de negócio
  - Transacionais (`@Transactional`)

**Regra de Ouro**: A Application Layer depende apenas do Domain Layer.

---

### 3. Infrastructure Layer (Camada de Infraestrutura)

**Responsabilidade**: Implementa adaptadores para tecnologias específicas e frameworks externos.

#### 📁 `infrastructure/persistence/entity/`
- **JPA Entities** para persistência
- `FeedbackEntity`, `CourseEntity`, `UserEntity`, `RoleEntity`
- Anotações JPA: `@Entity`, `@Table`, `@ManyToOne`, etc.
- Mapeamento para tabelas do banco de dados

#### 📁 `infrastructure/persistence/jpa/`
- **Spring Data JPA Repositories**
- `JpaFeedbackRepository`, `JpaCourseRepository`, `JpaUserRepository`
- Queries customizadas com `@Query` e fetch joins para performance
- `@EntityGraph` para eager loading otimizado

#### 📁 `infrastructure/persistence/mapper/`
- **Mappers** para conversão Entity ↔ Domain Model
- `FeedbackEntityMapper`: Converte entre `FeedbackEntity` e `Feedback`
- Implementa a separação entre modelo de persistência e modelo de domínio

#### 📁 `infrastructure/persistence/repository/`
- **Implementações** dos repositórios do domínio
- `FeedbackRepositoryImpl`: Implementa `FeedbackRepository` do domínio
- `CourseRepositoryImpl`: Implementa `CourseRepository` do domínio
- `UserRepositoryImpl`: Implementa `UserRepository` do domínio
- Adaptam entidades JPA para modelos de domínio

#### 📁 `infrastructure/pubsub/`
- **Adapter** para Google Cloud Pub/Sub
- `PubSubGatewayImpl`: Implementa `PubSubGateway` do domínio
- Utiliza `PubSubTemplate` do Spring Cloud GCP
- Publica eventos de feedback urgente no formato especificado

#### 📁 `config/`
- **Configurações Spring**
- `AppConfig`: Beans de configuração (ObjectMapper)
- `SecurityConfig`: Configuração de segurança (básica para desenvolvimento)

**Regra de Ouro**: A Infrastructure Layer implementa as interfaces definidas no Domain Layer.

---

### 4. Presentation Layer (Camada de Apresentação)

**Responsabilidade**: Interface HTTP REST, tratamento de requisições e respostas.

#### 📁 `presentation/controller/`
- **REST Controllers**
- `FeedbackController`: Endpoints para gerenciar feedbacks
- `CourseController`: Endpoints para listar cursos
- `GlobalExceptionHandler`: Tratamento centralizado de exceções

**Endpoints principais**:
- `POST /api/feedbacks` - Criar feedback
- `GET /api/feedbacks` - Listar feedbacks (com filtros)
- `GET /api/feedbacks/{id}` - Buscar feedback por ID
- `GET /api/courses` - Listar cursos
- `GET /api/courses/{id}` - Buscar curso por ID

**Regra de Ouro**: A Presentation Layer depende apenas da Application Layer.

---

## 🔄 Fluxo de Dados

### Exemplo: Criar Feedback Urgente

```
1. HTTP Request
   POST /api/feedbacks
   {
     "courseId": 1,
     "rating": 1,
     "comment": "Erro crítico",
     "isUrgent": true
   }
   ↓
   
2. FeedbackController
   - Valida DTO (@Valid)
   - Extrai userId do header
   - Chama CreateFeedbackUseCase
   ↓
   
3. CreateFeedbackUseCase
   - Busca Course via CourseRepository (Domain)
   - Busca User via UserRepository (Domain)
   - Cria Feedback (Domain Model)
   - Valida via FeedbackDomainService
   - Salva via FeedbackRepository (Domain)
   ↓
   
4. FeedbackRepositoryImpl (Infrastructure)
   - Converte Feedback → FeedbackEntity
   - Salva via JpaFeedbackRepository
   - Recarrega com relacionamentos
   - Converte FeedbackEntity → Feedback
   ↓
   
5. CreateFeedbackUseCase (continuação)
   - Verifica se é urgente via FeedbackDomainService
   - Se urgente, publica evento via PubSubGateway
   ↓
   
6. PubSubGatewayImpl (Infrastructure)
   - Converte Feedback para payload JSON
   - Publica no tópico Pub/Sub usando PubSubTemplate
   ↓
   
7. CreateFeedbackUseCase (continuação)
   - Converte Feedback → FeedbackResponse via FeedbackMapper
   - Retorna DTO
   ↓
   
8. FeedbackController
   - Retorna HTTP 201 Created com FeedbackResponse
```

---

## 🎯 Princípios Aplicados

### 1. Dependency Inversion Principle (DIP)
- **Domain Layer** define interfaces (`FeedbackRepository`, `PubSubGateway`)
- **Infrastructure Layer** implementa essas interfaces
- **Application Layer** depende apenas das interfaces, não das implementações

### 2. Single Responsibility Principle (SRP)
- Cada classe tem uma única responsabilidade
- Use Cases orquestram, Domain Services validam, Repositories persistem

### 3. Open/Closed Principle (OCP)
- Fácil adicionar novos Use Cases sem modificar existentes
- Fácil trocar implementações (ex: PostgreSQL → MongoDB) sem afetar domínio

### 4. Interface Segregation Principle (ISP)
- Interfaces específicas para cada necessidade
- `FeedbackRepository` não mistura responsabilidades de `CourseRepository`

### 5. Don't Repeat Yourself (DRY)
- Mappers centralizam lógica de conversão
- Domain Services centralizam regras de negócio
- Repositories centralizam lógica de persistência

---

## 🗄️ Modelo de Dados

### Entidades de Domínio

```
┌─────────────┐
│    Role     │
│─────────────│
│ roleId      │
│ name        │
│ description │
└──────┬──────┘
       │ 1
       │
       │ *
┌──────▼──────┐
│    User     │
│─────────────│
│ userId      │
│ name        │ (Value Object)
│ email       │ (Value Object)
│ role        │ ────┐
│ createdAt   │     │
└──────┬──────┘     │
       │ 1          │
       │            │
       │ *          │
┌──────▼────────────▼──┐
│      Feedback        │
│──────────────────────│
│ feedbackId           │
│ course               │ ────┐
│ user                 │     │
│ rating               │     │ (Value Object)
│ comment              │     │
│ isUrgent             │     │
│ createdAt            │     │
└──────┬───────────────┘     │
       │ *                    │
       │                      │
┌──────▼──────────┐           │
│    Course       │           │
│─────────────────│           │
│ courseId        │           │
│ title           │           │
│ description     │           │
│ createdAt       │           │
└─────────────────┘           │
                              │
```

### Value Objects

1. **Email**: Valida formato de e-mail
2. **Name**: Garante nome não vazio
3. **Rating**: Valida range 1-5

**Benefícios**:
- Validação garantida no domínio
- Imutabilidade
- Semântica clara

---

## 🔐 Segurança e Validação

### Validações por Camada

1. **Presentation Layer**:
   - Validação de formato (Bean Validation)
   - `@Valid`, `@NotNull`, `@Min`, `@Max`

2. **Application Layer**:
   - Validação de contexto (curso existe? usuário existe?)
   - Orquestração de validações

3. **Domain Layer**:
   - Validação de regras de negócio
   - Value Objects garantem invariantes
   - Domain Services validam lógica complexa

### Estado Atual de Segurança

⚠️ **Desenvolvimento**: Acesso público permitido
✅ **Produção**: Implementar:
- Autenticação JWT
- Autorização baseada em roles
- HTTPS obrigatório
- Rate limiting

---

## 🚀 Performance e Otimizações

### 1. Fetch Joins
- Repositórios JPA usam `LEFT JOIN FETCH` para evitar N+1 queries
- Carregam relacionamentos em uma única query

### 2. EntityGraph
- `@EntityGraph` para eager loading controlado
- Reduz número de queries ao banco

### 3. MapStruct
- Geração de código em tempo de compilação
- Zero overhead de reflection
- Type-safe mappers

### 4. Transações
- `@Transactional` apenas nos Use Cases
- Escopo controlado de transações
- Rollback automático em caso de erro

---

## 📦 Dependências Principais

| Dependência | Propósito |
|------------|-----------|
| Spring Boot | Framework base |
| Spring Data JPA | Persistência |
| PostgreSQL Driver | Banco de dados |
| Spring Cloud GCP Pub/Sub | Mensageria |
| MapStruct | Mapeamento DTO ↔ Domain |
| Lombok | Redução de boilerplate |
| Spring Security | Segurança (básica) |

---

## 🧪 Testabilidade

A arquitetura facilita testes:

1. **Domain Layer**: Testes unitários puros (sem mocks de frameworks)
2. **Application Layer**: Testes de integração com mocks de repositórios
3. **Infrastructure Layer**: Testes de integração com banco de dados
4. **Presentation Layer**: Testes de API com MockMvc

### Estratégia de Testes Recomendada

```
├── Domain Layer Tests (Unitários)
│   └── FeedbackDomainServiceTest
│
├── Application Layer Tests (Integração com Mocks)
│   └── CreateFeedbackUseCaseTest
│
├── Infrastructure Layer Tests (Integração Real)
│   └── FeedbackRepositoryImplTest
│
└── Presentation Layer Tests (API Tests)
    └── FeedbackControllerTest
```

---

## 🔄 Integração com Pub/Sub

Quando um feedback é marcado como urgente:

1. `CreateFeedbackUseCase` detecta `isUrgent = true`
2. Chama `FeedbackDomainService.shouldNotifyAdmins()`
3. Se verdadeiro, chama `PubSubGateway.publishFeedbackEvent()`
4. `PubSubGatewayImpl` converte Feedback para JSON conforme especificação
5. Publica no tópico `feedback-events` do Pub/Sub
6. Função serverless (já implementada) processa e envia notificação

**Formato do Evento**:
```json
{
  "rating": 1,
  "comment": "Erro crítico",
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

---

## 📈 Escalabilidade

### Pontos de Escalação

1. **Banco de Dados**:
   - Índices nas colunas mais consultadas
   - Particionamento futuro por data (se necessário)

2. **API**:
   - Stateless design permite horizontal scaling
   - Cache de cursos (se necessário)

3. **Pub/Sub**:
   - Processamento assíncrono
   - Múltiplas subscriptions para paralelismo

---

## 🛠️ Manutenibilidade

### Facilita Manutenção

1. **Separação de Responsabilidades**: Mudanças isoladas por camada
2. **Testes**: Cobertura por camada
3. **Documentação**: Código auto-documentado com nomes claros
4. **Padrões**: Consistência em todo o código

### Quando Adicionar Funcionalidades

**Exemplo: Adicionar avaliação de cursos**

1. **Domain**: Criar `CourseRating` entity
2. **Application**: Criar `CreateCourseRatingUseCase`
3. **Infrastructure**: Implementar `CourseRatingRepository`
4. **Presentation**: Criar `CourseRatingController`

**Sem modificar código existente** (Open/Closed Principle)

---

## 📚 Referências e Padrões

- **Clean Architecture**: Robert C. Martin (Uncle Bob)
- **Domain-Driven Design**: Eric Evans
- **Repository Pattern**: Martin Fowler
- **Use Case Pattern**: Ivar Jacobson

---

## 🎓 Aprendizados e Decisões

### Por que DDD?
- Código expressivo que reflete o negócio
- Facilita comunicação com stakeholders
- Regras de negócio centralizadas e testáveis

### Por que Clean Architecture?
- Independência de frameworks
- Testabilidade
- Flexibilidade para mudanças tecnológicas

### Por que MapStruct?
- Performance (sem reflection)
- Type-safety em tempo de compilação
- Redução de código boilerplate

---

## 🚦 Próximos Passos Arquiteturais

1. **CQRS**: Separar comandos (writes) de queries (reads)
2. **Event Sourcing**: Para auditoria completa
3. **Domain Events**: Desacoplamento entre contextos
4. **API Gateway**: Para múltiplos serviços
5. **Service Mesh**: Para comunicação entre microsserviços

---

**Última atualização**: 2025-01-15  
**Versão**: 1.0
