-- ============================================================
-- MIGRATION V2: CARGA INICIAL (SEED) - REDE RAIZES DO NORDESTE
-- ============================================================

-- Inserir Unidades
INSERT INTO tb_unidade (id, nome, endereco, cidade, uf, ativa) VALUES 
(1, 'Matriz Recife - Boa Viagem', 'Av. Conselheiro Aguiar, 1420', 'Recife', 'PE', true),
(2, 'Filial Recife - Derby', 'Rua Benfica, 305', 'Recife', 'PE', true),
(3, 'Filial Salvador - Barra', 'Av. Oceânica, 450', 'Salvador', 'BA', true),
(4, 'Filial São Paulo - Paulista', 'Av. Paulista, 1850', 'São Paulo', 'SP', true)
ON CONFLICT (id) DO NOTHING;

-- Inserir Produtos Regionais
INSERT INTO tb_produto (id, nome, descricao, preco, categoria, ativo) VALUES
(1, 'Cuscuz com Carne de Sol na Nata', 'Cuscuz de milho com carne de sol desfiada e molho de nata sertaneja.', 28.50, 'PRATO_PRINCIPAL', true),
(2, 'Tapioca Rendada com Queijo Coalho', 'Tapioca crocante com queijo coalho tostado na chapa e manteiga de garrafa.', 19.90, 'LANCHE', true),
(3, 'Bolo de Macaxeira Tradicional', 'Bolo cremoso de macaxeira com coco ralado fresco.', 12.00, 'SOBREMESA', true),
(4, 'Suco Natural de Graviola 400ml', 'Polpa fresca batida na hora, servido gelado.', 9.50, 'BEBIDA', true),
(5, 'Café Regional com Leite e Canela', 'Café coado com leite vaporizado e toque de canela do sertão.', 7.00, 'BEBIDA', true),
(6, 'Escondidinho de Charque Junino', 'Purê de macaxeira gratinado com queijo coalho e charque (Edição Junina).', 34.00, 'SAZONAL', true)
ON CONFLICT (id) DO NOTHING;

-- Inserir Estoque por Unidade
-- Unidade 1 (Recife Boa Viagem)
INSERT INTO tb_estoque_unidade (id, unidade_id, produto_id, quantidade_disponivel) VALUES
(1, 1, 1, 50),
(2, 1, 2, 40),
(3, 1, 3, 30),
(4, 1, 4, 60),
(5, 1, 5, 100),
(6, 1, 6, 25),

-- Unidade 2 (Recife Derby) - Item 1 com estoque baixo proposital para testes
(7, 2, 1, 2),
(8, 2, 2, 15),
(9, 2, 3, 10),
(10, 2, 4, 20),
(11, 2, 5, 40),
(12, 2, 6, 0),

-- Unidade 3 (Salvador Barra)
(13, 3, 1, 30),
(14, 3, 2, 25),
(15, 3, 3, 20),
(16, 3, 4, 35),
(17, 3, 5, 50),

-- Unidade 4 (São Paulo Paulista)
(18, 4, 1, 40),
(19, 4, 2, 35),
(20, 4, 3, 25),
(21, 4, 4, 45),
(22, 4, 5, 80)
ON CONFLICT (id) DO NOTHING;

-- Inserir Usuários (Senha padrão: Senha@123)
-- Hash BCrypt válido gerado para 'Senha@123'
INSERT INTO tb_usuario (id, nome, email, senha, perfil, telefone, ativo) VALUES
(1, 'Administrador do Sistema', 'admin@raizesdonordeste.com.br', '$2a$10$LlIhTqV7uQ5FrYujIcuF/e8pHyTd1NkDO7GwSOTCNebIpwghAy942', 'ADMIN', '81999990001', true),
(2, 'Gerente Carlos Recife', 'gerente.recife@raizesdonordeste.com.br', '$2a$10$LlIhTqV7uQ5FrYujIcuF/e8pHyTd1NkDO7GwSOTCNebIpwghAy942', 'GERENTE', '81999990002', true),
(3, 'Atendente Maria Balcao', 'atendente.derby@raizesdonordeste.com.br', '$2a$10$LlIhTqV7uQ5FrYujIcuF/e8pHyTd1NkDO7GwSOTCNebIpwghAy942', 'ATENDENTE', '81999990003', true),
(4, 'Cliente Ana Silva', 'ana.silva@email.com', '$2a$10$LlIhTqV7uQ5FrYujIcuF/e8pHyTd1NkDO7GwSOTCNebIpwghAy942', 'CLIENTE', '81988887777', true),
(5, 'Cliente Bruno Ferreira', 'bruno.ferreira@email.com', '$2a$10$LlIhTqV7uQ5FrYujIcuF/e8pHyTd1NkDO7GwSOTCNebIpwghAy942', 'CLIENTE', '81977776666', true)
ON CONFLICT (id) DO NOTHING;

-- Registro de Consentimento LGPD
INSERT INTO tb_consentimento_lgpd (id, usuario_id, finalidade, consentido, data_consentimento, ip_origem) VALUES
(1, 4, 'Cadastro para pedidos multicanal e programa de pontos na Rede Raizes do Nordeste', true, CURRENT_TIMESTAMP, '192.168.1.105'),
(2, 5, 'Cadastro para pedidos multicanal e programa de pontos na Rede Raizes do Nordeste', true, CURRENT_TIMESTAMP, '192.168.1.106')
ON CONFLICT (id) DO NOTHING;

-- Alinhar sequences no PostgreSQL
SELECT setval('tb_unidade_id_seq', (SELECT COALESCE(MAX(id), 1) FROM tb_unidade));
SELECT setval('tb_produto_id_seq', (SELECT COALESCE(MAX(id), 1) FROM tb_produto));
SELECT setval('tb_estoque_unidade_id_seq', (SELECT COALESCE(MAX(id), 1) FROM tb_estoque_unidade));
SELECT setval('tb_usuario_id_seq', (SELECT COALESCE(MAX(id), 1) FROM tb_usuario));
SELECT setval('tb_consentimento_lgpd_id_seq', (SELECT COALESCE(MAX(id), 1) FROM tb_consentimento_lgpd));
