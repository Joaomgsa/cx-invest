# Documentação do Projeto CX Invest

Visão geral rápida: este documento descreve a estrutura da API, o motor de recomendação, observações sobre segurança (link para `SECURITY.md`), tratamento de erros e estratégia de testes (unitários e integração).

Checklist de atividades cobertas neste documento:
- [x] Estrutura da API (endpoints principais e mapeamento para recursos/serviços)
- [x] Motor de recomendação (dados, algoritmo, mapeamentos)
- [x] Segurança (link para `SECURITY.md`) e tratamento de erros
- [x] Testes unitários e de integração (configuração e convenções)

---

## 1. Estrutura da API

O projeto segue um padrão REST simples com recursos organizados por responsabilidade. A documentação OpenAPI gerada fica em `src/main/openapi/openapi.yml`.

Principais recursos (endpoints) e onde encontrá-los:

- `/clientes` (recurso: `ClienteResource`) — operações: listar (GET), buscar por id (GET /{id}), criar (POST), atualizar (PUT /{id}), remover (DELETE /{id}). Conversões DTO ↔ entidade foram movidas para `ClienteService` (`toEntity`, `toResponse`).
  - DTOs: `ClienteRequest`, `ClienteResponse` em `br.com.cxinvest.dto.cliente`.
  - Dados de exemplo: `import.sql` (ex.: cliente id 1 — Ana Silva).

- `/produtos` (recurso: `ProdutoResource`) — listar, buscar por id, criar/atualizar/remover.
  - DTOs: `ProdutoRecomendadoResponse` (usado por recomendação) e outros records em `br.com.cxinvest.dto.produto`.
  - Dados de exemplo em `import.sql` (ex.: produto id 1 — "CDB Caixa 2026").

- `/produtos-recomendados/{perfilNome}` (recurso: `ProdutoService` / controller) — retorna produtos por nome do perfil (case-insensitive).

- `/simulacoes` (recursos: `SimulacaoResource` e `SimularResource`) — endpoints para listar histórico/agregados e para simular investimento:
  - `POST /simulacoes/simular-investimento` (recurso `SimularResource`) aceita `SimulacaoRequest` e retorna `SimulacaoResponse` (DTOs em `br.com.cxinvest.dto.simulacao`).
  - `GET /simulacoes/por-produto-dia` e `GET /simulacoes` para relatório/histórico.

- `/investimentos` (recurso: `InvestimentoResource`) — histórico de investimentos por cliente.

- `/telemetria` (recurso: `MetricResource` / `MetricsService`) — endpoints para obter telemetria agregada (usa `RequestMetricRepository`). Dados de exemplo em `import.sql`.

Observações técnicas:
- Repositórios usam Panache (Quarkus): `*Repository` classes.
- DTOs são records Java (localizados em `br.com.cxinvest.dto.*`).
- `import.sql` carrega dados de exemplo para dev/tests (perfils, clientes, produtos, simulações, investimentos, telemetria).
- OpenAPI: `src/main/openapi/openapi.yml`.

---

## 2. Motor de recomendação

Objetivo: recomendar produtos por perfil do cliente.

Arquitetura atual:
- Entrada: nome do perfil (`String perfilNome`) — endpoint `produtosRecomendadosPerfilNome` em `ProdutoService`.
- Dados: consulta `ProdutoRepository.listarProdutosPorNomePerfil(perfilNome)` que faz `LOWER(perfilInvestimento.nome) = ?1` no banco.
- Saída: `ProdutoRecomendadoResponse` (id, nome, tipo, rentabilidade, risco).

Mapeamento de risco:
- Implementação atual utiliza um enum `Risco` e o recurso `ProdutoService` mapeia o nome do perfil para risco (por exemplo: CONSERVADOR → Baixo, MODERADO → Médio, AGRESSIVO → Alto). Recomenda-se manter esse mapeamento centralizado (ex.: `Risco.fromPerfilNome(...)`) para consistência.

Recomendações de evolução (arquitetura):
- Se o volume aumentar, mover a lógica de recomendação para um componente separado (`RecommendationEngine`) com interface clara e injeção via CDI.
- Suportar regras configuráveis (pesos por rentabilidade, liquidez, idade do cliente, score) e permitir A/B testing.
- Persistir métricas de uso das recomendações (logs e telemetria) para análise.

Estratégia simples (atual):
- Filtragem por perfil (exata) + ordenação por rentabilidade ou heurística simples.
- Boa para MVP; trocar por modelos mais sofisticados (rankeamento por ML) se necessário.

---

## 3. Segurança e tratamento de erros

Segurança:
- Documentação e regra completa de segurança estão centralizadas em `SECURITY.md` (link: `SECURITY.md`).
- Endpoints usam anotações `@RolesAllowed(...)` nos resources (por exemplo, `SimularResource` permite `admin`, `analista`, `cliente`).
- Para desenvolvimento, existe um mecanismo de dev (filtro) que injeta autenticação admin ao consumir Swagger UI dev (arquivo `swagger-ui-dev.html` e `DevSecurityFilter`), ativado apenas em profile `dev`.

Tratamento de erros:
- A aplicação usa `ApiException` para erros de negócio com códigos HTTP apropriados (por exemplo, 400/404/500).
- Recomenda-se um handler global (ExceptionMapper<ApiException>) que converta exceções em respostas JSON padrão com formato:
  ```json
  {
    "status": 404,
    "error": "Not Found",
    "message": "Cliente não encontrado: 1",
    "timestamp": "2025-11-21T...Z"
  }
  ```
- Em SQL/native queries (ex.: `RequestMetricRepository.listarAgregadoPorPeriodo`) cuidado com tipos de colunas (`timestamp` armazenado como texto vs epoch millis) — no ambiente de teste isso já trouxe conversão problemáticas; coloque validação ou adaptadores para formatos esperados.

Boas práticas de erro:
- Não vazar stacktraces para o cliente em produção.
- Logar o erro com correlação (request-id) e retornar mensagem amigável ao cliente.
- Para validações, usar `ConstraintViolation` e retornar 400 com detalhes por campo.

---

## 4. Testes unitários e integração

Localização dos testes:
- Testes unitários e de serviço: `src/test/java/br/com/cxinvest/service/*Test.java`.
- Testes de recursos (segurança): `src/test/java/br/com/cxinvest/resource/*SecurityTest.java`.

Stack de testes e configurações:
- Framework: JUnit5 (Quarkus JUnit5), Quarkus Test + Mockito (`quarkus-junit5-mockito`).
- Banco em testes: H2 em memória configurado em `src/test/resources/application.properties` (usa `import.sql` para popular dados em testes/integration).
- Mocks: `@InjectMock` do Quarkus Test Mockito para injetar dependências nos testes de integração
  e `Mockito` para testes unitários isolados.

Dicas e convenções:
- Escrever testes unitários para lógica fixa nos services (ex.: `SimulacaoServiceImpl#calcularRentabilidadeFinal`, `PerfilService#definirPerfilCliente`).
- Testes de integração (QuarkusTest) para fluxos que usam o DB em memória e `import.sql` como fixtures. Quando a conversão de datas falhar (AttributeConverter), mockar repositórios específicos ou ajustar fixtures para formatos compatíveis.
- Criar testes parametrizados (`@ParameterizedTest`) para cobrir várias roles/ids com menos duplicação (já adotado em alguns security tests).

Comandos úteis (rodar localmente):

 - Rodar todos os testes:

```bash
mvn test
```

 - Rodar apenas testes de integração (por padrão os integration tests são controlados por profiles/plugins):

```bash
mvn -Dtest=*IntegrationTest test
```

Observações finais sobre qualidade de testes:
- Persistir fixtures determinísticas no `import.sql` com ids explícitos (já presente) facilita assertes nos testes.
- Para testar segurança com Keycloak, prefira usar `@TestSecurity` para testes unitários e reservar testes E2E para um ambiente que rode Keycloak (ou use Testcontainers).

---

## Apêndice — Melhorias Futuras

- Externalizar o motor de recomendação: extraia a lógica para um componente isolado com testes próprios.
- Adicionar um `ExceptionMapper<ApiException>` central para unificar formato de erro.
- Harden security: adicionar auditoria e logs de autorização (quem executou ação X).
- Considerar Testcontainers para rodar testes de integração com uma instância do banco mais próxima ao ambiente de produção (SQLite/Postgres conforme target).





