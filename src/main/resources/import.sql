-- Criação da tabela de perfis de investimento
CREATE TABLE IF NOT EXISTS tb_perfil_investimento (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  nome TEXT NOT NULL,
  pontuacao INTEGER NOT NULL,
  descricao TEXT
);

-- Inserir perfis padrão
INSERT INTO tb_perfil_investimento (id, nome, pontuacao, descricao) VALUES (1, 'CONSERVADOR', 10, 'Perfil com baixa tolerância a risco');
INSERT INTO tb_perfil_investimento (id, nome, pontuacao, descricao) VALUES (2, 'MODERADO', 50, 'Perfil com tolerância moderada a risco');
INSERT INTO tb_perfil_investimento (id, nome, pontuacao, descricao) VALUES (3, 'AGRESSIVO', 90, 'Perfil com alta tolerância a risco');

-- Criação da tabela cliente (para garantir que exista no banco SQLite) com referência ao perfil
CREATE TABLE IF NOT EXISTS tb_clientes (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  nome TEXT NOT NULL,
  email TEXT NOT NULL UNIQUE,
  total_investido NUMERIC NOT NULL DEFAULT 0,
  frequencia_investimento TEXT NOT NULL DEFAULT 'MEDIA',
  preferencia_investimento TEXT NOT NULL DEFAULT 'LIQUIDEZ',
  perfil_id INTEGER NOT NULL,
  status TEXT NOT NULL DEFAULT 'A',
  FOREIGN KEY (perfil_id) REFERENCES tb_perfil_investimento(id)
);

-- Inserir 10 clientes referenciando os perfis (1=CONSERVADOR,2=MODERADO,3=AGRESSIVO)
INSERT INTO tb_clientes (id, nome, email, total_investido, frequencia_investimento, preferencia_investimento, perfil_id, status) VALUES (1, 'Ana Silva', 'ana.silva@example.com', 1000.00, 'BAIXA', 'LIQUIDEZ', 1, 'A');
INSERT INTO tb_clientes (id, nome, email, total_investido, frequencia_investimento, preferencia_investimento, perfil_id, status) VALUES (2, 'Bruno Costa', 'bruno.costa@example.com', 2500.50, 'MEDIA', 'RENTABILIDADE', 2, 'A');
INSERT INTO tb_clientes (id, nome, email, total_investido, frequencia_investimento, preferencia_investimento, perfil_id, status) VALUES (3, 'Carla Souza', 'carla.souza@example.com', 500.00, 'ALTA', 'RENTABILIDADE', 3, 'A');
INSERT INTO tb_clientes (id, nome, email, total_investido, frequencia_investimento, preferencia_investimento, perfil_id, status) VALUES (4, 'Daniel Pereira', 'daniel.pereira@example.com', 1500.00, 'MEDIA', 'LIQUIDEZ', 1, 'A');
INSERT INTO tb_clientes (id, nome, email, total_investido, frequencia_investimento, preferencia_investimento, perfil_id, status) VALUES (5, 'Eduarda Lima', 'eduarda.lima@example.com', 3000.75, 'ALTA', 'RENTABILIDADE', 2, 'A');
INSERT INTO tb_clientes (id, nome, email, total_investido, frequencia_investimento, preferencia_investimento, perfil_id, status) VALUES (6, 'Felipe Rocha', 'felipe.rocha@example.com', 800.00, 'BAIXA', 'LIQUIDEZ', 3, 'A');
INSERT INTO tb_clientes (id, nome, email, total_investido, frequencia_investimento, preferencia_investimento, perfil_id, status) VALUES (7, 'Gabriela Martins', 'gabriela.martins@example.com', 1200.00, 'MEDIA', 'LIQUIDEZ', 1, 'A');
INSERT INTO tb_clientes (id, nome, email, total_investido, frequencia_investimento, preferencia_investimento, perfil_id, status) VALUES (8, 'Hugo Fernandes', 'hugo.fernandes@example.com', 2200.00, 'ALTA', 'RENTABILIDADE', 2, 'A');
INSERT INTO tb_clientes (id, nome, email, total_investido, frequencia_investimento, preferencia_investimento, perfil_id, status) VALUES (9, 'Isabela Nunes', 'isabela.nunes@example.com', 400.00, 'BAIXA', 'LIQUIDEZ', 3, 'A');
INSERT INTO tb_clientes (id, nome, email, total_investido, frequencia_investimento, preferencia_investimento, perfil_id, status) VALUES (10, 'João Oliveira', 'joao.oliveira@example.com', 1750.25, 'MEDIA', 'RENTABILIDADE', 2, 'A');

-- Criação da tabela de produtos conforme nova especificação com referência ao perfil
CREATE TABLE IF NOT EXISTS tb_produtos (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  nome TEXT NOT NULL,
  tipo TEXT NOT NULL,
  rentabilidade_mensal NUMERIC NOT NULL,
  perfil_id INTEGER NOT NULL,
  FOREIGN KEY (perfil_id) REFERENCES tb_perfil_investimento(id)
);

-- Inserir produtos de investimento na nova tabela tb_produtos com perfil_id
INSERT INTO tb_produtos (id, nome, tipo, rentabilidade_mensal, perfil_id) VALUES (1, 'CDB Caixa 2026', 'CDB', 0.12, 1);
INSERT INTO tb_produtos (id, nome, tipo, rentabilidade_mensal, perfil_id) VALUES (2, 'LCI Caixa Plus', 'LCI', 0.10, 1);
INSERT INTO tb_produtos (id, nome, tipo, rentabilidade_mensal, perfil_id) VALUES (3, 'LCA Agro', 'LCA', 0.11, 1);
INSERT INTO tb_produtos (id, nome, tipo, rentabilidade_mensal, perfil_id) VALUES (4, 'Tesouro Direto Selic', 'Tesouro Direto', 0.11, 1);
INSERT INTO tb_produtos (id, nome, tipo, rentabilidade_mensal, perfil_id) VALUES (5, 'Fundo XPTO', 'Fundo', 0.18, 3);
INSERT INTO tb_produtos (id, nome, tipo, rentabilidade_mensal, perfil_id) VALUES (6, 'Fundo Multimercado', 'Fundo', 0.15, 2);
INSERT INTO tb_produtos (id, nome, tipo, rentabilidade_mensal, perfil_id) VALUES (7, 'CDB Premium', 'CDB', 0.14, 2);
INSERT INTO tb_produtos (id, nome, tipo, rentabilidade_mensal, perfil_id) VALUES (8, 'Tesouro IPCA+', 'Tesouro Direto', 0.13, 2);

-- Criação da tabela de histórico de perfil do cliente
CREATE TABLE IF NOT EXISTS tb_cliente_perfil_historico (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  cliente_id INTEGER NOT NULL,
  perfil_anterior_id INTEGER,
  perfil_novo_id INTEGER NOT NULL,
  criadoEm TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  motivo VARCHAR(1000),
  metadados TEXT,
  pontuacaoNoMomento INTEGER,
  FOREIGN KEY (cliente_id) REFERENCES tb_clientes(id),
  FOREIGN KEY (perfil_anterior_id) REFERENCES tb_perfil_investimento(id),
  FOREIGN KEY (perfil_novo_id) REFERENCES tb_perfil_investimento(id)
);

-- Tabela de simulações (correspondente à entidade Simulacao)
CREATE TABLE IF NOT EXISTS tb_simulacoes (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  produto_id INTEGER NOT NULL,
  cliente_id INTEGER NOT NULL,
  rentabilidade_efetiva NUMERIC NOT NULL,
  valor_simulacao NUMERIC NOT NULL,
  valor_final NUMERIC NOT NULL,
  prazo_meses INTEGER NOT NULL,
  data_simulacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (produto_id) REFERENCES tb_produtos(id),
  FOREIGN KEY (cliente_id) REFERENCES tb_clientes(id)
);

-- Registros de exemplo em tb_simulacoes (compatíveis com a entidade Simulacao)
INSERT INTO tb_simulacoes (produto_id, cliente_id, rentabilidade_efetiva, valor_simulacao, valor_final, prazo_meses, data_simulacao) VALUES (1, 1, 0.08, 1000.00, 1100.50, 12, '2025-11-16 10:00:00.000');
INSERT INTO tb_simulacoes (produto_id, cliente_id, rentabilidade_efetiva, valor_simulacao, valor_final, prazo_meses, data_simulacao) VALUES (1, 2, 0.08, 1100.00, 1200.00, 12, '2025-11-16 15:30:00.000');
INSERT INTO tb_simulacoes (produto_id, cliente_id, rentabilidade_efetiva, valor_simulacao, valor_final, prazo_meses, data_simulacao) VALUES (1, 3, 0.075, 1000.00, 1050.75, 6, '2025-11-15 09:20:00.000');

INSERT INTO tb_simulacoes (produto_id, cliente_id, rentabilidade_efetiva, valor_simulacao, valor_final, prazo_meses, data_simulacao) VALUES (2, 4, 0.06, 950.00, 1000.00, 12, '2025-11-16 11:00:00.000');
INSERT INTO tb_simulacoes (produto_id, cliente_id, rentabilidade_efetiva, valor_simulacao, valor_final, prazo_meses, data_simulacao) VALUES (2, 5, 0.062, 1000.00, 1025.00, 12, '2025-11-16 12:45:00.000');

INSERT INTO tb_simulacoes (produto_id, cliente_id, rentabilidade_efetiva, valor_simulacao, valor_final, prazo_meses, data_simulacao) VALUES (5, 6, 0.18, 2200.00, 2500.00, 24, '2025-11-16 08:00:00.000');
INSERT INTO tb_simulacoes (produto_id, cliente_id, rentabilidade_efetiva, valor_simulacao, valor_final, prazo_meses, data_simulacao) VALUES (5, 7, 0.177, 2100.00, 2400.00, 24, '2025-11-15 14:10:00.000');

INSERT INTO tb_simulacoes (produto_id, cliente_id, rentabilidade_efetiva, valor_simulacao, valor_final, prazo_meses, data_simulacao) VALUES (8, 8, 0.13, 1200.00, 1300.00, 36, '2025-11-16 16:00:00.000');

-- Criação da tabela de investimentos (histórico de investimentos dos clientes)
CREATE TABLE IF NOT EXISTS tb_investimentos (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  cliente_id INTEGER NOT NULL,
  produto_id INTEGER NOT NULL,
  valor_investido NUMERIC NOT NULL,
  rentabilidade NUMERIC NOT NULL,
  data_investimento TIMESTAMP NOT NULL,
  FOREIGN KEY (cliente_id) REFERENCES tb_clientes(id),
  FOREIGN KEY (produto_id) REFERENCES tb_produtos(id)
);

-- Inserir 10 investimentos para cada cliente (clientes 1..10)
-- Os ids abaixo são explícitos para garantir previsibilidade nos testes
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (1, 1, 1, 1000.00, 0.012, '2025-01-05 09:00:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (2, 1, 2, 1500.00, 0.010, '2025-02-10 10:30:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (3, 1, 3, 1200.00, 0.011, '2025-03-12 14:20:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (4, 1, 4, 2000.00, 0.011, '2025-04-01 08:15:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (5, 1, 5, 2500.00, 0.018, '2025-05-20 12:00:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (6, 1, 6, 900.00, 0.015, '2025-06-15 16:45:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (7, 1, 7, 1100.00, 0.014, '2025-07-03 11:10:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (8, 1, 8, 1300.00, 0.013, '2025-08-21 09:50:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (9, 1, 1, 1400.00, 0.012, '2025-09-30 14:00:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (10, 1, 2, 1600.00, 0.010, '2025-10-05 10:00:00.000');

INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (11, 2, 3, 1100.00, 0.011, '2025-01-12 09:00:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (12, 2, 4, 2100.00, 0.011, '2025-02-18 10:15:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (13, 2, 5, 2300.00, 0.018, '2025-03-22 13:30:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (14, 2, 6, 1250.00, 0.015, '2025-04-11 15:00:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (15, 2, 7, 900.00, 0.014, '2025-05-02 08:30:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (16, 2, 8, 1750.00, 0.013, '2025-06-10 12:00:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (17, 2, 1, 1600.00, 0.012, '2025-07-19 11:45:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (18, 2, 2, 1800.00, 0.010, '2025-08-25 09:20:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (19, 2, 3, 1400.00, 0.011, '2025-09-14 14:10:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (20, 2, 4, 1950.00, 0.011, '2025-10-01 10:05:00.000');

INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (21, 3, 5, 800.00, 0.018, '2025-01-20 09:00:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (22, 3, 6, 950.00, 0.015, '2025-02-25 10:00:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (23, 3, 7, 700.00, 0.014, '2025-03-05 11:30:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (24, 3, 8, 600.00, 0.013, '2025-04-08 13:00:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (25, 3, 1, 1200.00, 0.012, '2025-05-14 09:45:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (26, 3, 2, 1000.00, 0.010, '2025-06-16 10:10:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (27, 3, 3, 850.00, 0.011, '2025-07-21 15:20:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (28, 3, 4, 900.00, 0.011, '2025-08-30 08:00:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (29, 3, 5, 1300.00, 0.018, '2025-09-12 12:00:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (30, 3, 6, 1400.00, 0.015, '2025-10-07 09:30:00.000');

INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (31, 4, 7, 1500.00, 0.014, '2025-01-08 10:00:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (32, 4, 8, 1300.00, 0.013, '2025-02-14 11:00:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (33, 4, 1, 1600.00, 0.012, '2025-03-18 14:30:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (34, 4, 2, 1100.00, 0.010, '2025-04-20 09:15:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (35, 4, 3, 1250.00, 0.011, '2025-05-22 16:00:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (36, 4, 4, 1700.00, 0.011, '2025-06-25 12:30:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (37, 4, 5, 1800.00, 0.018, '2025-07-29 08:45:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (38, 4, 6, 900.00, 0.015, '2025-08-31 10:10:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (39, 4, 7, 1050.00, 0.014, '2025-09-06 14:00:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (40, 4, 8, 1150.00, 0.013, '2025-10-02 11:20:00.000');

INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (41, 5, 1, 2000.00, 0.012, '2025-01-15 09:00:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (42, 5, 2, 2200.00, 0.010, '2025-02-17 10:30:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (43, 5, 3, 2400.00, 0.011, '2025-03-19 13:15:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (44, 5, 4, 2600.00, 0.011, '2025-04-21 15:40:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (45, 5, 5, 2800.00, 0.018, '2025-05-23 08:55:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (46, 5, 6, 1200.00, 0.015, '2025-06-27 12:10:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (47, 5, 7, 1350.00, 0.014, '2025-07-30 11:00:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (48, 5, 8, 1450.00, 0.013, '2025-08-05 09:20:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (49, 5, 1, 1550.00, 0.012, '2025-09-08 14:35:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (50, 5, 2, 1650.00, 0.010, '2025-10-11 10:05:00.000');

INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (51, 6, 3, 800.00, 0.011, '2025-01-03 09:10:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (52, 6, 4, 950.00, 0.011, '2025-02-06 10:25:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (53, 6, 5, 1200.00, 0.018, '2025-03-09 13:40:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (54, 6, 6, 700.00, 0.015, '2025-04-12 15:55:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (55, 6, 7, 850.00, 0.014, '2025-05-15 08:05:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (56, 6, 8, 950.00, 0.013, '2025-06-18 11:15:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (57, 6, 1, 1000.00, 0.012, '2025-07-21 09:40:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (58, 6, 2, 1150.00, 0.010, '2025-08-24 14:25:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (59, 6, 3, 1250.00, 0.011, '2025-09-27 10:50:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (60, 6, 4, 1350.00, 0.011, '2025-10-29 12:00:00.000');

INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (61, 7, 5, 900.00, 0.018, '2025-01-18 09:30:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (62, 7, 6, 1000.00, 0.015, '2025-02-20 10:45:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (63, 7, 7, 1100.00, 0.014, '2025-03-23 13:55:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (64, 7, 8, 1150.00, 0.013, '2025-04-25 16:20:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (65, 7, 1, 1250.00, 0.012, '2025-05-27 08:50:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (66, 7, 2, 1300.00, 0.010, '2025-06-29 11:05:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (67, 7, 3, 1400.00, 0.011, '2025-07-31 14:30:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (68, 7, 4, 1500.00, 0.011, '2025-08-02 09:15:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (69, 7, 5, 1600.00, 0.018, '2025-09-04 12:40:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (70, 7, 6, 1700.00, 0.015, '2025-10-06 10:00:00.000');

INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (71, 8, 7, 1800.00, 0.014, '2025-01-07 09:05:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (72, 8, 8, 1900.00, 0.013, '2025-02-09 10:20:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (73, 8, 1, 2000.00, 0.012, '2025-03-11 13:35:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (74, 8, 2, 2100.00, 0.010, '2025-04-13 15:50:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (75, 8, 3, 2200.00, 0.011, '2025-05-15 08:25:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (76, 8, 4, 2300.00, 0.011, '2025-06-17 11:40:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (77, 8, 5, 2400.00, 0.018, '2025-07-19 14:55:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (78, 8, 6, 2500.00, 0.015, '2025-08-21 09:30:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (79, 8, 7, 2600.00, 0.014, '2025-09-23 12:10:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (80, 8, 8, 2700.00, 0.013, '2025-10-25 10:50:00.000');

INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (81, 9, 1, 300.00, 0.012, '2025-01-02 09:00:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (82, 9, 2, 400.00, 0.010, '2025-02-04 10:10:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (83, 9, 3, 500.00, 0.011, '2025-03-06 13:20:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (84, 9, 4, 600.00, 0.011, '2025-04-08 15:30:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (85, 9, 5, 700.00, 0.018, '2025-05-10 08:40:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (86, 9, 6, 800.00, 0.015, '2025-06-12 11:55:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (87, 9, 7, 900.00, 0.014, '2025-07-14 14:05:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (88, 9, 8, 1000.00, 0.013, '2025-08-16 09:25:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (89, 9, 1, 1100.00, 0.012, '2025-09-18 12:35:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (90, 9, 2, 1200.00, 0.010, '2025-10-20 10:15:00.000');

INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (91, 10, 3, 1300.00, 0.011, '2025-01-25 09:50:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (92, 10, 4, 1400.00, 0.011, '2025-02-27 11:05:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (93, 10, 5, 1500.00, 0.018, '2025-03-29 13:15:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (94, 10, 6, 1600.00, 0.015, '2025-04-30 15:25:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (95, 10, 7, 1700.00, 0.014, '2025-05-31 08:10:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (96, 10, 8, 1800.00, 0.013, '2025-06-30 11:40:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (97, 10, 1, 1900.00, 0.012, '2025-07-30 14:55:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (98, 10, 2, 2000.00, 0.010, '2025-08-29 09:30:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (99, 10, 3, 2100.00, 0.011, '2025-09-28 12:20:00.000');
INSERT INTO tb_investimentos (id, cliente_id, produto_id, valor_investido, rentabilidade, data_investimento) VALUES (100, 10, 4, 2200.00, 0.011, '2025-10-27 10:00:00.000');

-- Criação da tabela de telemetria (eventos)
CREATE TABLE IF NOT EXISTS tb_telemetria_events (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  servico TEXT NOT NULL,
  tempo_resposta_ms INTEGER NOT NULL,
  data_evento TEXT NOT NULL
);

-- Índice para acelerar consultas por período e serviço (data_evento, servico)
CREATE INDEX IF NOT EXISTS idx_telemetria_data_servico ON tb_telemetria_events (data_evento, servico);

-- Inserir eventos de telemetria de exemplo (outubro/2025)
INSERT INTO tb_telemetria_events (servico, tempo_resposta_ms, data_evento) VALUES ('simular-investimento', 240, '2025-10-01 10:00:00.000');
INSERT INTO tb_telemetria_events (servico, tempo_resposta_ms, data_evento) VALUES ('simular-investimento', 260, '2025-10-05 11:15:00.000');
INSERT INTO tb_telemetria_events (servico, tempo_resposta_ms, data_evento) VALUES ('simular-investimento', 255, '2025-10-10 09:30:00.000');
INSERT INTO tb_telemetria_events (servico, tempo_resposta_ms, data_evento) VALUES ('simular-investimento', 245, '2025-10-15 14:00:00.000');
INSERT INTO tb_telemetria_events (servico, tempo_resposta_ms, data_evento) VALUES ('simular-investimento', 250, '2025-10-20 08:45:00.000');

INSERT INTO tb_telemetria_events (servico, tempo_resposta_ms, data_evento) VALUES ('perfil-risco', 170, '2025-10-02 12:10:00.000');
INSERT INTO tb_telemetria_events (servico, tempo_resposta_ms, data_evento) VALUES ('perfil-risco', 190, '2025-10-08 13:20:00.000');
INSERT INTO tb_telemetria_events (servico, tempo_resposta_ms, data_evento) VALUES ('perfil-risco', 180, '2025-10-18 15:40:00.000');
INSERT INTO tb_telemetria_events (servico, tempo_resposta_ms, data_evento) VALUES ('perfil-risco', 185, '2025-10-25 09:05:00.000');
INSERT INTO tb_telemetria_events (servico, tempo_resposta_ms, data_evento) VALUES ('perfil-risco', 185, '2025-10-25 09:05:00.000');

-- Criação da tabela para RequestMetric (filtro de métricas de requisição)
CREATE TABLE IF NOT EXISTS request_metrics (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  path TEXT NOT NULL,
  method TEXT NOT NULL,
  status INTEGER NOT NULL,
  response_time_ms INTEGER NOT NULL,
  timestamp TEXT NOT NULL
);



-- Inserir alguns exemplos
INSERT INTO request_metrics (path, method, status, response_time_ms, timestamp) VALUES ('/simulacao', 'POST', 200, 240, '2025-10-01T10:00:00Z');
INSERT INTO request_metrics (path, method, status, response_time_ms, timestamp) VALUES ('/perfil-risco', 'GET', 200, 180, '2025-10-02T12:10:00Z');

