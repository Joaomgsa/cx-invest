# cx-invest

Painel de Investimentos com Perfil de Risco Dinâmico (Quarkus, Java 21)

Este repositório contém a API backend para simulações e recomendações de produtos de investimento.

Sumário
- Visão geral
- Estrutura da API e documentação
- Qualidade do motor de recomendação
- Segurança e tratamento de erros
- Testes unitários e de integração
- Execução (dev / empacotamento)

Visão geral

A aplicação foi desenvolvida com Quarkus (Java 21). Fornece endpoints para simulação de investimentos, histórico, telemetria e recomendação de produtos com base no perfil de risco do cliente.

Estrutura da API e documentação

- Endpoints principais:
  - POST /simular-investimento — solicita uma simulação de investimento (clienteId, valor, prazoMeses, tipoProduto)
  - GET /simulacoes — lista todas as simulações
  - GET /simulacoes/por-produto-dia — agregados diários por produto
  - GET /telemetria — métricas de requisições (volumes e tempos)
  - GET /perfil-risco/{clienteId} — retorna perfil de risco calculado
  - GET /produtos-recomendados/{perfil} — produtos recomendados para um perfil
  - GET /investimentos/{clienteId} — histórico de investimentos do cliente

- Documentação OpenAPI/Swagger:
  - A API expõe a especificação OpenAPI. Em modo `dev` a UI está disponível em: http://localhost:8080/q/dev/ e /q/openapi (dependendo da versão do Quarkus)
  - Os modelos (DTOs) e exemplos são gerados a partir dos DTOs Java e dos dados de `imports.sql` usados como seed no ambiente de desenvolvimento.

Qualidade do motor de recomendação

- Objetivo: combinar regras de negócio (volume, frequência e preferência por liquidez) com filtros de produto para indicar os produtos mais adequados ao perfil do cliente.
- Estratégia de validação e qualidade:
  - Regras unitárias cobertas por testes (validação de elegibilidade do produto por perfil, limites de prazo/valor, mapeamento de risco).
  - Integração com dados de histórico e persistência (cenários de sucesso são cobertos por testes de integração).
  - Uso de scoring simples e explicável (pontuação por critério) para permitir rastreabilidade do motivo da recomendação.
  - Conversão de níveis de risco para rótulos de negócio (ex.: Conservador -> Baixo, Moderado -> Médio, Agressivo -> Alto) utilizando Enum do projeto.

Segurança e tratamento de erros

- Autenticação/Autorização:
  - A aplicação está preparada para usar Keycloak (OIDC). Papéis (roles) previstos: `admin`, `analista`, `cliente`.
  - As regras de acesso são aplicadas via anotações de segurança (ex.: `@RolesAllowed`) nos recursos.
  - Para instruções detalhadas de configuração do Keycloak e criação dos clients/roles/users, consulte o arquivo `SECURITY.md`.

- Tratamento de erros:
  - Exceções de negócio lançam `ApiException` com código HTTP apropriado (ex.: 404 para não encontrado, 400 para entrada inválida, 403 para acesso negado).
  - Erros inesperados retornam 500 com um corpo padronizado contendo `httpCode` e `mensagem`.
  - Endpoints de validação e persistência fazem validações preventivas e reportam mensagens claras para facilitar debugging.

Testes unitários e de integração

- Testes unitários:
  - Cobrem lógicas de negócio isoladas (services, mapeamentos, validações). Utilizam mocks e matchers para reduzir acoplamento.
  - Testes parametrizados foram adotados para reduzir duplicação e cobrir variações de papéis/perfis.

- Testes de integração:
  - Validam o comportamento dos services conectados ao banco em cenários de sucesso.
  - São executados com o banco embutido (SQLite) e usam os dados de `imports.sql` como seed.

Como contribuir / links úteis

- Segurança detalhada: `SECURITY.md`
- Especificação do projeto e decisões arquiteturais: `projeto.md` (referência interna — ver para detalhes sobre estrutura, motor de recomendação, segurança e testes)

Execução (modo desenvolvimento)

Pré-requisitos: Java 21, Maven 3.x, Docker (para infra: Keycloak, Prometheus, Grafana)

Rodar Keycloak (exemplo com Docker Compose):

```powershell
# a partir do diretório do projeto - servicos de infra (keycloak, prometheus, grafana)
docker-compose -f docker-compose.yml up --build
```

Rodar a aplicação em dev mode (live coding):

```powershell
./mvnw quarkus:dev
```

Pacote e execução do jar:

```powershell
./mvnw package
java -jar target/quarkus-app/quarkus-run.jar
```

Rodar a Aplicaçao em container em devmode(Docker):

```powershell
# a partir do diretório do projeto
docker compose -f docker-compose.dev.yml up --build

Observações

- O arquivo `imports.sql` contém exemplos de dados usados nos testes e na documentação OpenAPI. Não reimporte os dados em um banco já populado (scripts de inicialização do container contemplam checagem).
- Para dúvidas sobre como habilitar o Swagger UI com um usuário admin em dev, veja `SECURITY.md`.

---


