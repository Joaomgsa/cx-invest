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
  FOREIGN KEY (perfil_id) REFERENCES tb_perfil_investimento(id)
);

-- Inserir 10 clientes referenciando os perfis (1=CONSERVADOR,2=MODERADO,3=AGRESSIVO)
INSERT INTO tb_clientes (id, nome, email, total_investido, frequencia_investimento, preferencia_investimento, perfil_id) VALUES (1, 'Ana Silva', 'ana.silva@example.com', 1000.00, 'BAIXA', 'LIQUIDEZ', 1);
INSERT INTO tb_clientes (id, nome, email, total_investido, frequencia_investimento, preferencia_investimento, perfil_id) VALUES (2, 'Bruno Costa', 'bruno.costa@example.com', 2500.50, 'MEDIA', 'RENTABILIDADE', 2);
INSERT INTO tb_clientes (id, nome, email, total_investido, frequencia_investimento, preferencia_investimento, perfil_id) VALUES (3, 'Carla Souza', 'carla.souza@example.com', 500.00, 'ALTA', 'RENTABILIDADE', 3);
INSERT INTO tb_clientes (id, nome, email, total_investido, frequencia_investimento, preferencia_investimento, perfil_id) VALUES (4, 'Daniel Pereira', 'daniel.pereira@example.com', 1500.00, 'MEDIA', 'LIQUIDEZ', 1);
INSERT INTO tb_clientes (id, nome, email, total_investido, frequencia_investimento, preferencia_investimento, perfil_id) VALUES (5, 'Eduarda Lima', 'eduarda.lima@example.com', 3000.75, 'ALTA', 'RENTABILIDADE', 2);
INSERT INTO tb_clientes (id, nome, email, total_investido, frequencia_investimento, preferencia_investimento, perfil_id) VALUES (6, 'Felipe Rocha', 'felipe.rocha@example.com', 800.00, 'BAIXA', 'LIQUIDEZ', 3);
INSERT INTO tb_clientes (id, nome, email, total_investido, frequencia_investimento, preferencia_investimento, perfil_id) VALUES (7, 'Gabriela Martins', 'gabriela.martins@example.com', 1200.00, 'MEDIA', 'LIQUIDEZ', 1);
INSERT INTO tb_clientes (id, nome, email, total_investido, frequencia_investimento, preferencia_investimento, perfil_id) VALUES (8, 'Hugo Fernandes', 'hugo.fernandes@example.com', 2200.00, 'ALTA', 'RENTABILIDADE', 2);
INSERT INTO tb_clientes (id, nome, email, total_investido, frequencia_investimento, preferencia_investimento, perfil_id) VALUES (9, 'Isabela Nunes', 'isabela.nunes@example.com', 400.00, 'BAIXA', 'LIQUIDEZ', 3);
INSERT INTO tb_clientes (id, nome, email, total_investido, frequencia_investimento, preferencia_investimento, perfil_id) VALUES (10, 'João Oliveira', 'joao.oliveira@example.com', 1750.25, 'MEDIA', 'RENTABILIDADE', 2);

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
INSERT INTO tb_simulacoes (produto_id, cliente_id, rentabilidade_efetiva, valor_simulacao, valor_final, prazo_meses, data_simulacao) VALUES (1, 1, 0.08, 1000.00, 1100.50, 12, '2025-11-16T10:00:00Z');
INSERT INTO tb_simulacoes (produto_id, cliente_id, rentabilidade_efetiva, valor_simulacao, valor_final, prazo_meses, data_simulacao) VALUES (1, 2, 0.08, 1100.00, 1200.00, 12, '2025-11-16T15:30:00Z');
INSERT INTO tb_simulacoes (produto_id, cliente_id, rentabilidade_efetiva, valor_simulacao, valor_final, prazo_meses, data_simulacao) VALUES (1, 3, 0.075, 1000.00, 1050.75, 6, '2025-11-15T09:20:00Z');

INSERT INTO tb_simulacoes (produto_id, cliente_id, rentabilidade_efetiva, valor_simulacao, valor_final, prazo_meses, data_simulacao) VALUES (2, 4, 0.06, 950.00, 1000.00, 12, '2025-11-16T11:00:00Z');
INSERT INTO tb_simulacoes (produto_id, cliente_id, rentabilidade_efetiva, valor_simulacao, valor_final, prazo_meses, data_simulacao) VALUES (2, 5, 0.062, 1000.00, 1025.00, 12, '2025-11-16T12:45:00Z');

INSERT INTO tb_simulacoes (produto_id, cliente_id, rentabilidade_efetiva, valor_simulacao, valor_final, prazo_meses, data_simulacao) VALUES (5, 6, 0.18, 2200.00, 2500.00, 24, '2025-11-16T08:00:00Z');
INSERT INTO tb_simulacoes (produto_id, cliente_id, rentabilidade_efetiva, valor_simulacao, valor_final, prazo_meses, data_simulacao) VALUES (5, 7, 0.177, 2100.00, 2400.00, 24, '2025-11-15T14:10:00Z');

INSERT INTO tb_simulacoes (produto_id, cliente_id, rentabilidade_efetiva, valor_simulacao, valor_final, prazo_meses, data_simulacao) VALUES (8, 8, 0.13, 1200.00, 1300.00, 36, '2025-11-16T16:00:00Z');
