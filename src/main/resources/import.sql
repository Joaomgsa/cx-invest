-- Criação da tabela cliente (para garantir que exista no banco SQLite)
CREATE TABLE IF NOT EXISTS tb_clientes (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  nome TEXT NOT NULL,
  email TEXT NOT NULL UNIQUE,
  perfil_risco TEXT NOT NULL
);

-- Inserir 10 tb_clientess
INSERT INTO tb_clientes (id, nome, email, perfil_risco) VALUES (1, 'Ana Silva', 'ana.silva@example.com', 'CONSERVADOR');
INSERT INTO tb_clientes (id, nome, email, perfil_risco) VALUES (2, 'Bruno Costa', 'bruno.costa@example.com', 'MODERADO');
INSERT INTO tb_clientes (id, nome, email, perfil_risco) VALUES (3, 'Carla Souza', 'carla.souza@example.com', 'AGRESSIVO');
INSERT INTO tb_clientes (id, nome, email, perfil_risco) VALUES (4, 'Daniel Pereira', 'daniel.pereira@example.com', 'CONSERVADOR');
INSERT INTO tb_clientes (id, nome, email, perfil_risco) VALUES (5, 'Eduarda Lima', 'eduarda.lima@example.com', 'MODERADO');
INSERT INTO tb_clientes (id, nome, email, perfil_risco) VALUES (6, 'Felipe Rocha', 'felipe.rocha@example.com', 'AGRESSIVO');
INSERT INTO tb_clientes (id, nome, email, perfil_risco) VALUES (7, 'Gabriela Martins', 'gabriela.martins@example.com', 'CONSERVADOR');
INSERT INTO tb_clientes (id, nome, email, perfil_risco) VALUES (8, 'Hugo Fernandes', 'hugo.fernandes@example.com', 'MODERADO');
INSERT INTO tb_clientes (id, nome, email, perfil_risco) VALUES (9, 'Isabela Nunes', 'isabela.nunes@example.com', 'AGRESSIVO');
INSERT INTO tb_clientes (id, nome, email, perfil_risco) VALUES (10, 'João Oliveira', 'joao.oliveira@example.com', 'MODERADO');

-- Criação da tabela de produtos conforme nova especificação
CREATE TABLE IF NOT EXISTS tb_produtos (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  nome TEXT NOT NULL,
  tipo TEXT NOT NULL,
  rentabilidade_mensal NUMERIC NOT NULL,
  classe_risco TEXT NOT NULL
);

-- Inserir produtos de investimento na nova tabela tb_produtos
INSERT INTO tb_produtos (id, nome, tipo, rentabilidade_mensal, classe_risco) VALUES (1, 'CDB Caixa 2026', 'CDB', 0.12, 'CONSERVADOR');
INSERT INTO tb_produtos (id, nome, tipo, rentabilidade_mensal, classe_risco) VALUES (2, 'LCI Caixa Plus', 'LCI', 0.10, 'CONSERVADOR');
INSERT INTO tb_produtos (id, nome, tipo, rentabilidade_mensal, classe_risco) VALUES (3, 'LCA Agro', 'LCA', 0.11, 'CONSERVADOR');
INSERT INTO tb_produtos (id, nome, tipo, rentabilidade_mensal, classe_risco) VALUES (4, 'Tesouro Direto Selic', 'Tesouro Direto', 0.11, 'CONSERVADOR');
INSERT INTO tb_produtos (id, nome, tipo, rentabilidade_mensal, classe_risco) VALUES (5, 'Fundo XPTO', 'Fundo', 0.18, 'AGRESSIVO');
INSERT INTO tb_produtos (id, nome, tipo, rentabilidade_mensal, classe_risco) VALUES (6, 'Fundo Multimercado', 'Fundo', 0.15, 'MODERADO');
INSERT INTO tb_produtos (id, nome, tipo, rentabilidade_mensal, classe_risco) VALUES (7, 'CDB Premium', 'CDB', 0.14, 'MODERADO');
INSERT INTO tb_produtos (id, nome, tipo, rentabilidade_mensal, classe_risco) VALUES (8, 'Tesouro IPCA+', 'Tesouro Direto', 0.13, 'MODERADO');

-- Inserir alguns investimentos de exemplo para cliente 123
INSERT INTO investimentos (id, cliente_id, tipo, valor, rentabilidade, data) VALUES (1, 123, 'CDB', 5000.00, 0.12, '2025-01-15');
INSERT INTO investimentos (id, cliente_id, tipo, valor, rentabilidade, data) VALUES (2, 123, 'Fundo Multimercado', 3000.00, 0.08, '2025-03-10');
INSERT INTO investimentos (id, cliente_id, tipo, valor, rentabilidade, data) VALUES (3, 123, 'LCI', 8000.00, 0.10, '2025-05-20');