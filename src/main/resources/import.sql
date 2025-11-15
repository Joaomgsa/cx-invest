-- Inserir produtos de investimento
INSERT INTO produtos (id, nome, tipo, rentabilidade, risco, prazo_min_meses, prazo_max_meses, valor_minimo) VALUES (1, 'CDB Caixa 2026', 'CDB', 0.12, 'Baixo', 6, 60, 1000.00);
INSERT INTO produtos (id, nome, tipo, rentabilidade, risco, prazo_min_meses, prazo_max_meses, valor_minimo) VALUES (2, 'LCI Caixa Plus', 'LCI', 0.10, 'Baixo', 12, 36, 5000.00);
INSERT INTO produtos (id, nome, tipo, rentabilidade, risco, prazo_min_meses, prazo_max_meses, valor_minimo) VALUES (3, 'LCA Agro', 'LCA', 0.11, 'Baixo', 12, 48, 3000.00);
INSERT INTO produtos (id, nome, tipo, rentabilidade, risco, prazo_min_meses, prazo_max_meses, valor_minimo) VALUES (4, 'Tesouro Direto Selic', 'Tesouro Direto', 0.11, 'Baixo', 1, 120, 100.00);
INSERT INTO produtos (id, nome, tipo, rentabilidade, risco, prazo_min_meses, prazo_max_meses, valor_minimo) VALUES (5, 'Fundo XPTO', 'Fundo', 0.18, 'Alto', 6, 36, 1000.00);
INSERT INTO produtos (id, nome, tipo, rentabilidade, risco, prazo_min_meses, prazo_max_meses, valor_minimo) VALUES (6, 'Fundo Multimercado', 'Fundo', 0.15, 'Médio', 12, 48, 2000.00);
INSERT INTO produtos (id, nome, tipo, rentabilidade, risco, prazo_min_meses, prazo_max_meses, valor_minimo) VALUES (7, 'CDB Premium', 'CDB', 0.14, 'Médio', 24, 60, 10000.00);
INSERT INTO produtos (id, nome, tipo, rentabilidade, risco, prazo_min_meses, prazo_max_meses, valor_minimo) VALUES (8, 'Tesouro IPCA+', 'Tesouro Direto', 0.13, 'Médio', 12, 240, 500.00);

-- Inserir alguns investimentos de exemplo para cliente 123
INSERT INTO investimentos (id, cliente_id, tipo, valor, rentabilidade, data) VALUES (1, 123, 'CDB', 5000.00, 0.12, '2025-01-15');
INSERT INTO investimentos (id, cliente_id, tipo, valor, rentabilidade, data) VALUES (2, 123, 'Fundo Multimercado', 3000.00, 0.08, '2025-03-10');
INSERT INTO investimentos (id, cliente_id, tipo, valor, rentabilidade, data) VALUES (3, 123, 'LCI', 8000.00, 0.10, '2025-05-20');