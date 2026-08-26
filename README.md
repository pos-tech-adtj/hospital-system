# Hospital System - Tech Challenge FIAP Fase 3

## Arquitetura

O projeto possui dois serviços Spring Boot:

- `api-agendamento` (porta `8081`): autenticação, cadastro/edição de consultas e histórico via GraphQL.
- `api-notificacao` (porta `8082`): consumo de eventos RabbitMQ, envio de lembretes e consulta das notificações via GraphQL.

Os serviços usam PostgreSQL e RabbitMQ. O banco é compartilhado, mas cada serviço possui seu schema: `agendamento` e `notificacao`. O serviço de notificações lê os usuários diretamente de `agendamento.usuarios` para autenticar os mesmos usuários do serviço de agendamento; não existe uma segunda base de usuários.

## Execução

Pré-requisitos: Docker e Docker Compose.

```bash
docker compose up --build
```

Serviços:

- Agendamento: `http://localhost:8081/graphql`
- Notificações: `http://localhost:8082/graphql`
- Health checks: `http://localhost:8081/actuator/health` e `http://localhost:8082/actuator/health`
- Painel RabbitMQ: `http://localhost:15672`

As credenciais padrão do ambiente são `hospital`/`hospital` para PostgreSQL e RabbitMQ. Elas podem ser alteradas por variáveis de ambiente. Os usuários da aplicação são carregados pela migration do serviço de agendamento, com senha armazenada usando BCrypt.

## Segurança e permissões

As APIs GraphQL usam autenticação HTTP Basic, sessão stateless e acesso autenticado. As permissões são aplicadas com Spring Security:

| Operação | Médico | Enfermeiro | Paciente |
|---|---|---|---|
| `historicoConsultas` | Visualizar | Visualizar | Apenas próprias consultas |
| `agendamentosPorPaciente` | Visualizar | Visualizar | Apenas quando o ID é o próprio |
| `registrarConsulta` | Registrar | Registrar | Não permitido |
| `editarConsulta` | Editar | Editar | Não permitido |
| `cancelarConsulta` | Cancelar | Cancelar | Não permitido |
| `notificacoesPorPaciente` | Consultar | Consultar | Apenas próprias notificações |
| `notificacoesPorConsulta` | Consultar | Consultar | Apenas notificações próprias |

## GraphQL

O serviço de agendamento disponibiliza `historicoConsultas`, com filtro opcional por status e `apenasFuturas`, além de `agendamentosPorPaciente`, `registrarConsulta`, `editarConsulta` e `cancelarConsulta`.

O serviço de notificações disponibiliza `notificacoesPorPaciente` e `notificacoesPorConsulta`. As notificações são persistidas com os status `PENDENTE`, `ENVIADA` ou `FALHA`.

## Comunicação assíncrona

Após o commit de uma alteração de consulta, o agendamento publica eventos `consulta.criada`, `consulta.atualizada` ou `consulta.lembrete` no exchange `consultas.exchange`. O serviço de notificações consome esses eventos pela fila `notificacao.consulta.queue`, envia a mensagem por mock ou e-mail e registra o resultado.

Um scheduler procura consultas agendadas nas próximas 24 horas e publica o evento de lembrete. A fila possui retry e dead-letter queue para mensagens rejeitadas.

O envio ao RabbitMQ ocorre após o commit; portanto, uma garantia transacional completa entre PostgreSQL e RabbitMQ exigiria um padrão Outbox, que não faz parte da implementação atual.

## Collections

- [Hospital-System.postman_collection.json](collection%20temporaria/Hospital-System.postman_collection.json): health checks, autenticação e operações do agendamento.
- [Agendamento-Autorizacao.postman_collection.json](postman/Agendamento-Autorizacao.postman_collection.json): validação do isolamento de pacientes.
- [Notificacao-GraphQL.postman_collection.json](postman/Notificacao-GraphQL.postman_collection.json): consultas do serviço de notificações.

As collections usam autenticação Basic e variáveis para URLs, credenciais e IDs.
