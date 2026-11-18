-- ============================================================
-- MIGRATION V1: CRIAÇÃO DE TABELAS - REDE RAIZES DO NORDESTE
-- Projeto Multidisciplinar UNINTER - Trilha Back-End
-- ============================================================

-- 1. Tabela de Unidades (Filiais e Matriz)
CREATE TABLE IF NOT EXISTS tb_unidade (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    endereco VARCHAR(200) NOT NULL,
    cidade VARCHAR(80) NOT NULL,
    uf VARCHAR(2) NOT NULL,
    ativa BOOLEAN NOT NULL DEFAULT true
);

-- 2. Tabela de Produtos (Catálogo Regional)
CREATE TABLE IF NOT EXISTS tb_produto (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    descricao TEXT,
    preco DECIMAL(10,2) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT true
);

-- 3. Tabela de Estoque por Unidade (Controle descentralizado)
CREATE TABLE IF NOT EXISTS tb_estoque_unidade (
    id BIGSERIAL PRIMARY KEY,
    unidade_id BIGINT NOT NULL,
    produto_id BIGINT NOT NULL,
    quantidade_disponivel INTEGER NOT NULL,
    CONSTRAINT fk_estoque_unidade FOREIGN KEY (unidade_id) REFERENCES tb_unidade(id) ON DELETE RESTRICT,
    CONSTRAINT fk_estoque_produto FOREIGN KEY (produto_id) REFERENCES tb_produto(id) ON DELETE RESTRICT,
    CONSTRAINT uk_estoque_unidade_produto UNIQUE (unidade_id, produto_id)
);

-- 4. Tabela de Usuários (RBAC: CLIENTE, ATENDENTE, GERENTE, ADMIN)
CREATE TABLE IF NOT EXISTS tb_usuario (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    perfil VARCHAR(20) NOT NULL,
    telefone VARCHAR(20),
    ativo BOOLEAN NOT NULL DEFAULT true
);

-- 5. Tabela de Consentimento LGPD (Art. 7º, I da Lei 13.709/2018)
CREATE TABLE IF NOT EXISTS tb_consentimento_lgpd (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    finalidade VARCHAR(255) NOT NULL,
    consentido BOOLEAN NOT NULL DEFAULT true,
    data_consentimento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_origem VARCHAR(45),
    CONSTRAINT fk_consentimento_usuario FOREIGN KEY (usuario_id) REFERENCES tb_usuario(id) ON DELETE CASCADE
);

-- 6. Tabela de Pedidos (Multicanalidade obrigatória: canal_pedido)
CREATE TABLE IF NOT EXISTS tb_pedido (
    id BIGSERIAL PRIMARY KEY,
    unidade_id BIGINT NOT NULL,
    cliente_id BIGINT NOT NULL,
    canal_pedido VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL,
    valor_total DECIMAL(10,2) NOT NULL,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pedido_unidade FOREIGN KEY (unidade_id) REFERENCES tb_unidade(id) ON DELETE RESTRICT,
    CONSTRAINT fk_pedido_cliente FOREIGN KEY (cliente_id) REFERENCES tb_usuario(id) ON DELETE RESTRICT
);

-- 7. Tabela de Itens de Pedido
CREATE TABLE IF NOT EXISTS tb_item_pedido (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    produto_id BIGINT NOT NULL,
    quantidade INTEGER NOT NULL,
    preco_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_item_pedido FOREIGN KEY (pedido_id) REFERENCES tb_pedido(id) ON DELETE CASCADE,
    CONSTRAINT fk_item_produto FOREIGN KEY (produto_id) REFERENCES tb_produto(id) ON DELETE RESTRICT
);

-- 8. Tabela de Pagamentos Desacoplados (Gateway Mock)
CREATE TABLE IF NOT EXISTS tb_pagamento (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT NOT NULL UNIQUE,
    transacao_externa_id VARCHAR(100),
    forma_pagamento VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    data_processamento TIMESTAMP,
    mensagem_retorno VARCHAR(255),
    CONSTRAINT fk_pagamento_pedido FOREIGN KEY (pedido_id) REFERENCES tb_pedido(id) ON DELETE CASCADE
);

-- 9. Tabela de Trilha de Auditoria (Operações Sensíveis)
CREATE TABLE IF NOT EXISTS tb_auditoria_log (
    id BIGSERIAL PRIMARY KEY,
    acao VARCHAR(50) NOT NULL,
    entidade VARCHAR(50) NOT NULL,
    entidade_id BIGINT,
    usuario_responsavel VARCHAR(150) NOT NULL,
    detalhes TEXT,
    data_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices para otimização de consultas frequentes em horários de pico
CREATE INDEX IF NOT EXISTS idx_pedido_canal ON tb_pedido(canal_pedido);
CREATE INDEX IF NOT EXISTS idx_pedido_status ON tb_pedido(status);
CREATE INDEX IF NOT EXISTS idx_estoque_unidade_prod ON tb_estoque_unidade(unidade_id, produto_id);
CREATE INDEX IF NOT EXISTS idx_auditoria_data ON tb_auditoria_log(data_hora DESC);
