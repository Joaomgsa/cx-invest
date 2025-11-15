
# PRD: Painel de Investimentos com Perfil de Risco Dinâmico

**Documento de Requisitos do Produto**

---

## 1. Visão Geral

### 1.1. Objetivo do Produto
[span_0](start_span)Criar uma aplicação web que analisa o comportamento financeiro do cliente e ajusta automaticamente seu perfil de risco[span_0](end_span). [span_1](start_span)Esta aplicação deve sugerir produtos de investimento adequados, como CDBs, LCIs, LCAs, Tesouro Direto, Fundos, etc.[span_1](end_span).

### 1.2. Contexto e Problema
[span_2](start_span)A CAIXA está em processo de evolução da sua plataforma de investimentos digital[span_2](end_span). [span_3](start_span)O objetivo deste desafio é disponibilizar uma API para todos os brasileiros que permita a simulação de investimentos[span_3](end_span).

A API esperada deve ser capaz de:
* [span_4](start_span)Retornar um perfil de risco com base em dados financeiros[span_4](end_span).
* [span_5](start_span)Sugerir produtos de investimento adequados ao perfil e ao histórico de investimentos do cliente[span_5](end_span).
* [span_6](start_span)Simular investimentos com base em valor, prazo e tipo de produto[span_6](end_span).

---

## 2. Requisitos Funcionais

### 2.1. Simulação de Investimentos
A API deve permitir que um usuário simule um investimento.
* [span_7](start_span)Deve receber uma solicitação de simulação de investimentos via JSON[span_7](end_span).
* [span_8](start_span)Deve validar os dados de entrada da API (valor, prazo, tipo) consultando parâmetros de produtos em um banco de dados[span_8](end_span).
* [span_9](start_span)Deve filtrar e identificar qual produto de investimento se adequa aos parâmetros de entrada fornecidos[span_9](end_span).
* [span_10](start_span)Deve realizar os cálculos de simulação para o tipo de investimento selecionado[span_10](end_span).
* [span_11](start_span)Deve retornar um JSON contendo o produto que foi validado e o resultado da simulação[span_11](end_span).
* [span_12](start_span)A simulação realizada deve ser persistida em um banco local[span_12](end_span).

### 2.2. Motor de Recomendação e Perfil de Risco
A API deve incluir um motor de recomendação para definir o perfil de risco do cliente.
* O algoritmo de perfil deve ser baseado em:
    * [span_13](start_span)Volume de investimentos[span_13](end_span)
    * [span_14](start_span)Frequência de movimentações[span_14](end_span)
    * [span_15](start_span)Preferência por liquidez ou rentabilidade[span_15](end_span)
* A pontuação definirá os seguintes perfis:
    * **[span_16](start_span)Conservador:** Baixa movimentação, foco em liquidez[span_16](end_span).
    * **[span_17](start_span)Moderado:** Equilíbrio entre liquidez e rentabilidade[span_17](end_span).
    * **[span_18](start_span)Agressivo:** Busca por alta rentabilidade, maior risco[span_18](end_span).

### 2.3. Consultas e Relatórios
A API deve fornecer endpoints para consulta de dados históricos e agregados.
* [span_19](start_span)Deve haver um endpoint para retornar **todas** as simulações realizadas[span_19](end_span).
* [span_20](start_span)Deve haver um endpoint para retornar os valores simulados de forma agregada por produto e por dia[span_20](end_span).

---

## 3. Requisitos Não Funcionais e Técnicos

### 3.1. Stack de Tecnologia
* **[span_21](start_span)Linguagem:** Java 21 ou C# (.Net) 8+[span_21](end_span).
* **[span_22](start_span)Banco de Dados:** SQL Server ou SQLite[span_22](end_span).

### 3.2. Segurança
* [span_23](start_span)A API deve implementar autenticação via **JWT, OAuth2 ou Keycloak**[span_23](end_span).

### 3.3. Observabilidade (Telemetria)
* [span_24](start_span)A API deve possuir um endpoint que retorne dados de telemetria, especificamente volumes (quantidade de chamadas) e tempos de resposta para cada serviço[span_24](end_span).

### 3.4. DevOps e Entrega
* **[span_25](start_span)Código Fonte:** O código fonte completo deve ser disponibilizado, seja em formato .zip ou via um link para um repositório Git público[span_25](end_span).
* **[span_26](start_span)Containerização:** O projeto deve incluir todos os arquivos necessários para execução via container (ex: `dockerfile` / `Docker-compose`)[span_26](end_span).

---

## 4. Especificação da API (Endpoints e Modelos)

### 4.1. Simular Investimento
* **[span_27](start_span)Endpoint:** `POST /simular-investimento`[span_27](end_span)
* **[span_28](start_span)Request:** [cite: 33-38]
    ```json
    {
      "clienteId": 123,
      "valor": 10000.00,
      "prazoMeses": 12,
      "tipoProduto": "CDB"
    }
    ```
* [cite_start]**Response (Sucesso):** [cite: 40-54]
    ```json
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

### 4.2. Histórico de Simulações Realizadas
* [cite_start]**Endpoint:** `GET /simulacoes`[span_28](end_span)
* **[span_29](start_span)Response:** [cite: 58-77]
    ```json
    [
      {
        "id": 1,
        "clienteId": 123,
        "produto": "CDB Caixa 2026",
        "valorInvestido": 10000.00,
        "valorFinal": 11200.00,
        "prazoMeses": 12,
        "dataSimulacao": "2025-10-31T14:00:00Z"
      },
      {
        "id": 2,
        "clienteId": 123,
        "produto": "Fundo XPTO",
        "valorInvestido": 5000.00,
        "valorFinal": 5800.00,
        "prazoMeses": 6,
        "dataSimulacao": "2025-09-15T10:30:00Z"
      }
    ]
    ```

### 4.3. Valores Simulados por Produto e Dia
* [cite_start]**Endpoint:** `GET /simulacoes/por-produto-dia`[span_29](end_span)
* **[span_30](start_span)Response:** [cite: 81-93]
    ```json
    [
      {
        "produto": "CDB Caixa 2026",
        "data": "2025-10-30",
        "quantidadeSimulacoes": 15,
        "mediaValorFinal": 11050.00
      },
      {
        "produto": "Fundo XPTO",
        "data": "2025-10-30",
        "quantidadeSimulacoes": 8,
        "mediaValorFinal": 5700.00
      }
    ]
    ```

### 4.4. Dados de Telemetria
* [cite_start]**Endpoint:** `GET /telemetria`[span_30](end_span)
* **[span_31](start_span)Response:** [cite: 95-112]
    ```json
    {
      "servicos": [
        {
          "nome": "simular-investimento",
          "quantidadeChamadas": 120,
          "mediaTempoRespostaMs": 250
        },
        {
          "nome": "perfil-risco",
          "quantidadeChamadas": 80,
          "mediaTempoRespostaMs": 180
        }
      ],
      "periodo": {
        "inicio": "2025-10-01",
        "fim": "2025-10-31"
      }
    }
    ```

### 4.5. Perfil de Risco do Cliente
* [cite_start]**Endpoint:** `GET /perfil-risco/{clienteId}`[span_31](end_span)
* **[span_32](start_span)Response:** [cite: 116-121]
    ```json
    {
      "clienteId": 123,
      "perfil": "Moderado",
      "pontuacao": 65,
      "descricao": "Perfil equilibrado entre segurança e rentabilidade."
    }
    ```

### 4.6. Produtos Recomendados por Perfil
* [cite_start]**Endpoint:** `GET /produtos-recomendados/{perfil}`[span_32](end_span)
* **[span_33](start_span)Response:** [cite: 125-140]
    ```json
    [
      {
        "id": 101,
        "nome": "CDB Caixa 2026",
        "tipo": "CDB",
        "rentabilidade": 0.12,
        "risco": "Baixo"
      },
      {
        "id": 102,
        "nome": "Fundo XPTO",
        "tipo": "Fundo",
        "rentabilidade": 0.18,
        "risco": "Alto"
      }
    ]
    ```

### 4.7. Histórico de Investimentos do Cliente
* [cite_start]**Endpoint:** `GET /investimentos/{clienteId}`[span_33](end_span)
* **[span_34](start_span)Response:** [cite: 144-159]
    ```json
    [
      {
        "id": 1,
        "tipo": "CDB",
        "valor": 5000.00,
        "rentabilidade": 0.12,
        "data": "2025-01-15"
      },
      {
        "id": 2,
        "tipo": "Fundo Multimercado",
        "valor": 3000.00,
        "rentabilidade": 0.08,
        "data": "2025-03-10"
      }
    ]
    ```

---

## 5. Critérios de Avaliação
O sucesso do projeto será avaliado com base nos seguintes critérios:
* [cite_start]Estrutura da API e documentação[span_34](end_span)
* [span_35](start_span)Qualidade do motor de recomendação[span_35](end_span)
* [span_36](start_span)Segurança e tratamento de erros[span_36](end_span)
* [span_37](start_span)Testes unitários e integração[span_37](end_span)

