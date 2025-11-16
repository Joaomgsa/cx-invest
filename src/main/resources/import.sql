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
