# Hospital System - Tech Challenge FIAP

Backend para gerenciamento de agendamento de consultas hospitalares, com autenticação por perfil de usuário, consulta de histórico médico via GraphQL e envio de lembretes automáticos via comunicação assíncrona, desenvolvido como tech challenge da FIAP (Fase 3).

## Arquitetura

O projeto é composto por dois microsserviços Spring Boot independentes, cada um com seu próprio schema no banco e sua própria API GraphQL:

| Serviço | Porta | Responsabilidade |
|---|---|---|
| `api-agendamento` | `8081` | Autenticação, cadastro/edição/cancelamento de consultas e consulta do histórico médico via GraphQL. Publica eventos no RabbitMQ quando uma consulta é criada, atualizada ou está próxima (lembrete). |
| `api-notificacao` | `8082` | Consome os eventos do RabbitMQ, registra e envia os lembretes aos pacientes, e expõe consulta das notificações via GraphQL. |

Cada serviço segue uma estrutura em camadas:

- `api/graphql` — resolvers GraphQL (controllers)
- `dto` — objetos de entrada e saída
- `service` — regras de negócio
- `domain` — entidades e enums de domínio
- `repository` — acesso a dados (Spring Data JPA)
- `security` — autenticação e principal do usuário autenticado
- `config` — configurações de segurança, GraphQL, RabbitMQ e scheduler
- `exception` — exceções de domínio e tratamento de erros do GraphQL

Os dois serviços compartilham a mesma instância do PostgreSQL, mas usam schemas isolados (`agendamento` e `notificacao`) — não há acoplamento de tabelas entre eles. O `api-notificacao` lê os usuários (para autenticação) a partir das mesmas linhas gravadas pela migration do `api-agendamento`; não existe uma segunda base de usuários.

> **Nota:** o desafio lista um serviço de histórico como opcional, podendo ser separado do agendamento. Optamos por não criar um terceiro microsserviço para isso — o histórico de consultas é exposto como parte do `api-agendamento` (query `historicoConsultas`), já que os dados de consulta pertencem naturalmente a esse domínio.

## Comunicação assíncrona

- Exchange: `consultas.exchange` (topic), publicado pelo `api-agendamento`.
- Ao criar, editar, cancelar ou identificar uma consulta próxima (lembrete), um evento (`consulta.criada`, `consulta.atualizada` ou `consulta.lembrete`) é publicado **somente após o commit** da transação no banco, evitando notificar alterações que sofreram rollback.
- O `api-notificacao` consome pela fila `notificacao.consulta.queue`, processa de forma idempotente (dedupe por `eventId`), envia a notificação (mock ou e-mail, configurável) e persiste o resultado.
- Reprocessamento: até 3 tentativas com backoff exponencial; mensagens que continuam falhando vão para a dead-letter queue `notificacao.consulta.dlq`.
- Schedulers no `api-agendamento` varrem periodicamente as consultas para disparar lembretes das próximas 24h e cancelar automaticamente consultas com data expirada.

## Segurança e permissões

As APIs GraphQL usam autenticação HTTP Basic e sessão stateless. As permissões por perfil são aplicadas com `@PreAuthorize` do Spring Security e reforçadas na camada de serviço:

| Operação | Médico | Enfermeiro | Paciente |
|---|---|---|---|
| `historicoConsultas` | Visualizar tudo | Visualizar tudo | Apenas as próprias consultas |
| `agendamentosPorPaciente` | Visualizar de qualquer paciente | Visualizar de qualquer paciente | Apenas quando `idPaciente` é o próprio ID |
| `registrarConsulta` | Registrar | Registrar | Não permitido |
| `editarConsulta` | Editar | Editar | Não permitido |
| `cancelarConsulta` | Cancelar | Cancelar | Não permitido |
| `notificacoesPorPaciente` | Consultar | Consultar | Apenas as próprias notificações |
| `notificacoesPorConsulta` | Consultar | Consultar | Apenas notificações da própria consulta |

## Endpoints GraphQL

### `api-agendamento` (`http://localhost:8081/graphql`)

| Operação | Tipo | Descrição | Acesso |
|---|---|---|---|
| `usuarioAutenticado` | Query | Retorna dados do usuário logado e suas authorities | Autenticado |
| `historicoConsultas(filter)` | Query | Lista consultas, com filtro opcional por `status` e `apenasFuturas` | Autenticado (paciente vê só as próprias) |
| `agendamentosPorPaciente(idPaciente)` | Query | Lista consultas de um paciente | Autenticado (paciente só o próprio ID) |
| `registrarConsulta(input)` | Mutation | Cria uma nova consulta | Médico, Enfermeiro |
| `editarConsulta(idConsulta, input)` | Mutation | Edita data/status/especialidade/observações de uma consulta | Médico, Enfermeiro |
| `cancelarConsulta(idConsulta)` | Mutation | Cancela uma consulta agendada | Médico, Enfermeiro |

### `api-notificacao` (`http://localhost:8082/graphql`)

| Operação | Tipo | Descrição | Acesso |
|---|---|---|---|
| `notificacoesPorPaciente` | Query | Lista notificações do paciente autenticado | Autenticado |
| `notificacoesPorConsulta(idConsulta)` | Query | Lista notificações de uma consulta | Autenticado (paciente só as próprias) |

Rotas marcadas como **Autenticado** exigem HTTP Basic Auth (usuário/senha), validado contra os usuários carregados pela migration do `api-agendamento`. As únicas rotas públicas são `/actuator/health` de cada serviço.

## Pré-requisitos

- [Docker](https://www.docker.com/) e Docker Compose instalados

## Como executar

### 1. Clone o repositório

```bash
git clone https://github.com/pos-tech-adtj/hospital-system.git
```

### 2. Configure as variáveis de ambiente (opcional)

As credenciais padrão já funcionam out-of-the-box. Caso queira customizar, defina as variáveis abaixo (via `.env` ou ambiente):

```env
# PostgreSQL
POSTGRES_USER=hospital
POSTGRES_PASSWORD=hospital
POSTGRES_DB=hospital_db

# RabbitMQ
RABBITMQ_USER=hospital
RABBITMQ_PASSWORD=hospital
```

### 3. Suba a aplicação com Docker Compose

```bash
docker compose up --build
```

Isso irá:
- Subir o PostgreSQL 16 na porta `5432`
- Subir o RabbitMQ (com painel de gerenciamento) na porta `15672`
- Buildar as imagens dos dois serviços com Maven e executá-los em containers
- Iniciar os serviços somente após o banco e o RabbitMQ ficarem saudáveis
- Executar as migrações do Flyway automaticamente na subida de cada serviço
- Expor o `api-agendamento` na porta `8081` e o `api-notificacao` na porta `8082`

### 4. Acesse a aplicação

| Recurso | URL |
|---|---|
| Agendamento (GraphQL) | `http://localhost:8081/graphql` |
| Notificações (GraphQL) | `http://localhost:8082/graphql` |
| Health check - Agendamento | `http://localhost:8081/actuator/health` |
| Health check - Notificações | `http://localhost:8082/actuator/health` |
| Painel RabbitMQ | `http://localhost:15672` (usuário/senha: `hospital` / `hospital`) |

## Collections Postman

Importe os exports da pasta [`postman/`](./postman) na raiz do projeto:

- [`Hospital-System-GraphQL.postman_collection.json`](./postman/Hospital-System-GraphQL.postman_collection.json) — collection oficial, organizada por serviço e perfil.
- [`Hospital-System-Local.postman_environment.json`](./postman/Hospital-System-Local.postman_environment.json) — ambiente local com host, portas, credenciais e IDs de exemplo.

## Variáveis de ambiente da aplicação

Caso queira rodar os serviços fora do Docker Compose, configure as seguintes variáveis:

| Variável | Padrão (`api-agendamento`) | Padrão (`api-notificacao`) |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/hospital_db` | `jdbc:postgresql://localhost:5432/hospital_db` |
| `SPRING_DATASOURCE_USERNAME` | `hospital` | `hospital` |
| `SPRING_DATASOURCE_PASSWORD` | `hospital` | `hospital` |
| `SPRING_RABBITMQ_HOST` | `rabbitmq` | `rabbitmq` |
| `SPRING_RABBITMQ_USERNAME` | `hospital` | `hospital` |
| `SPRING_RABBITMQ_PASSWORD` | `hospital` | `hospital` |
| `SERVER_PORT` | `8081` | `8082` |
