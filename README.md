# Hospital System - Tech Challenge FIAP Fase 3

## Matriz de permissoes

| Operacao | MEDICO | ENFERMEIRO | PACIENTE |
|---|---|---|---|
| `historicoConsultas` | Pode visualizar historico de consultas | Pode acessar historico de consultas | Pode visualizar apenas as proprias consultas |
| `historicoConsultasPorPaciente(idPaciente)` | Pode visualizar historico por paciente | Pode visualizar historico por paciente | Apenas quando `idPaciente` for o proprio usuario autenticado |
| `registrarConsulta` (mutation) | Nao permitido | Pode registrar consultas | Nao permitido |
| `editarConsulta` (mutation) | Pode editar historico de consultas | Nao permitido | Nao permitido |

### Regras de seguranca aplicadas

- `@EnableMethodSecurity` habilitado.
- `@PreAuthorize` aplicado em cada query e mutation.
- Acesso indevido deve retornar erro GraphQL classificado como `FORBIDDEN`.
