# Documento de Projeto — cx-invest

Este documento descreve a arquitetura e decisões principais do projeto CX Invest.

1. Estrutura da API

A API é organizada por recursos REST que modelam clientes, produtos, simulações, perfil de risco e telemetria. A especificação OpenAPI está em `src/main/openapi/openapi.yml` e contém descrições e exemplos para requests/responses.

Endpoints principais (resumo):
- POST /simulacoes/simular-investimento — simula um investimento (req: `SimulacaoRequest`, resp: `SimulacaoResponse`).
  - Exemplo (do `imports.sql`):
    {
      "clienteId": 1,
      "valor": 1000.00,
      "prazoMeses": 12,
      "tipoProduto": "CDB"
    }
- GET /simulacoes — lista simulações (paginado opcionalmente)
- GET /simulacoes/por-produto-dia — agregações por produto/dia
- GET /telemetria — telemetria agregada por path (parâmetros `inicio`, `fim`, `page`, `size`)
- GET /produtos-recomendados/{perfilNome} — busca produtos por nome do perfil (case-insensitive)
- GET /produtos e GET /produtos/{id} — lista e busca de produtos
- GET /clientes e GET /clientes/{id} — lista e busca de clientes

OpenAPI (observações):
- Os exemplos de request/response no `openapi.yml` foram extraídos dos DTOs e do `quarkus/imports.sql` para refletir dados reais de seed.
- Foi adicionado exemplo para `SimulacaoRequest` com clienteId `1` (Ana Silva) e produto `CDB Caixa 2026`.

2. Motor de recomendação

Descrição:
- O motor aplica filtros por `tipoProduto`, `perfilInvestimento` e regras de elegibilidade (prazo/valor).
- Produz uma lista ordenada de `ProdutoRecomendadoResponse` contendo: id, nome, tipo, rentabilidade, risco (mapeado do perfil: Conservador -> Baixo, Moderado -> Médio, Agressivo -> Alto).

Qualidade e métricas:
- Cobertura de testes unitários para regras de negócio críticas (mapear risco, cálculo de elegibilidade).
- Testes de integração validam fluxo end-to-end do `Service` com banco em memória (SQLite) usando `imports.sql`.
- Recomenda-se adicionar métricas de precisão/recall se houver ground-truth histórico de adesões.

3. Segurança (link)

A autenticação e autorização são feitas via Keycloak (OIDC). Para mais detalhes e passos de configuração veja `SECURITY.md`.

4. Tratamento de erros

Padrões:
- Exceções de negócio -> `ApiException` com `httpCode` e `mensagem`.
- Validações -> retornam 400 com payload contendo as mensagens de validação.
- Não encontrado -> 404.
- Acesso negado -> 403.
- Erros inesperados -> 500 com payload `{ "httpCode": 500, "mensagem": "..." }`.

5. Testes

- Unitários: mocks e matchers, testes parametrizados para cenários de roles e inputs.
- Integração: services testados em cenário feliz (dados do `imports.sql`), testes para telemetria e simulações usando seeds.

6. Observações operacionais

- O arquivo `quarkus/imports.sql` contém os seeds para perfis, clientes, produtos e simulações. Em containers o entrypoint deve verificar se as tabelas já existem antes de executar os inserts para evitar duplicação.
- Em dev, a UI do Quarkus Dev pode ser usada para inspeção do OpenAPI e para testar endpoints com o usuário admin (ver `SECURITY.md` para configuração do cliente e mapeamento de roles).

---

Arquivo gerado automaticamente a partir do repositório para documentar a arquitetura e decisões principais.
