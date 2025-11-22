

# Product Requirements Document (PRD)

## Projeto
Painel de Investimentos com Perfil de Risco Dinâmico

## Objetivo
Criar uma aplicação web que analisa o comportamento financeiro do cliente e ajusta automaticamente seu perfil de risco, sugerindo produtos de investimento como CDBs, LCIs, LCAs, Tesouro Direto, Fundos, etc.

## Descrição do Produto
Desenvolvimento de uma API para simulação de investimentos que retorne o perfil de risco baseado em dados financeiros, produtos de investimento mais adequados ao perfil e histórico de investimentos. A aplicação deve permitir simulação de investimentos com entrada de valor, prazo e tipo.

## Tecnologias
- Linguagem: Java 21 ou C# (.NET 8+)
- Banco de Dados: SQL Server ou SQLite
- Contêiner: Docker (Dockerfile e Docker Compose)
- Autenticação: JWT, OAuth2 ou Keycloak

## Funcionalidades Principais

### API de Simulação de Investimentos
- Endpoint: `POST /simular-investimento`
- Receber um envelope JSON com dados da solicitação de simulação (clienteId, valor, prazoMeses, tipoProduto).
- Consultar informações parametrizadas no banco de dados.
- Validar dados de entrada conforme parâmetros dos produtos.
- Filtrar produtos adequados.
- Realizar cálculos de simulação conforme tipo de investimento.
- Retornar JSON com o produto validado e resultado da simulação.
- Persistir simulação realizada no banco local.

### Endpoints Adicionais
- `GET /simulacoes`: Retornar todas as simulações realizadas.
- `GET /simulacoes/por-produto-dia`: Retornar valores simulados para cada produto em cada dia.
- `GET /telemetria`: Retornar dados de telemetria com volumes e tempos de resposta para cada serviço.
- `GET /perfil-risco/{clienteId}`: Retornar perfil de risco do cliente.
- `GET /produtos-recomendados/{perfil}`: Retornar produtos recomendados para determinado perfil.
- `GET /investimentos/{clienteId}`: Retornar histórico de investimentos do cliente.

## Motor de Recomendação
- Baseado em:
  - Volume de investimentos
  - Frequência de movimentações
  - Preferência por liquidez ou rentabilidade
- Pontuação para definir perfil de risco:
  - Conservador: baixa movimentação, foco em liquidez
  - Moderado: equilíbrio entre liquidez e rentabilidade
  - Agressivo: busca por alta rentabilidade, maior risco

## Modelos de Envelope JSON para API

### Exemplo Solicitação de Simulação de Investimento (POST /simular-investimento)
```

{
"clienteId": 123,
"valor": 10000.00,
"prazoMeses": 12,
"tipoProduto": "CDB"
}

```

### Exemplo de Resposta
```

{
"produtoValidado": {
"id": 101,
"nome": "CDB Caixa 2026",
"tipo": "CDB",
"rentabilidade": 0.12,
"risco": "Baixo"
},
"resultadoSimulacao": {
"valorFinal": 11200.00,
"rentabilidadeEfetiva": 0.12,
"prazoMeses": 12
},
"dataSimulacao": "2025-10-31T14:00:00Z"
}

```

## Critérios de Avaliação
- Estrutura da API e documentação
- Qualidade do motor de recomendação
- Segurança e tratamento de erros
- Testes unitários e de integração

## Requisitos Não Funcionais
- O código-fonte deve ser disponibilizado com evidências no formato ZIP ou arquivo texto contendo link para repositório público Git.
- O projeto deve estar configurado para execução via contêiner Docker.
- Autenticação segura usando JWT, OAuth2 ou Keycloak.
```


