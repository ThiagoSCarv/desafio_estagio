-- =============================================================================
-- Seed: dados de exemplo para desenvolvimento
-- Schema: desafio_estagio
-- UUIDs armazenados como BINARY(16) pelo Hibernate 6 + MySQLDialect
-- =============================================================================

USE desafio_estagio;

-- -----------------------------------------------------------------------------
-- Clientes PF
-- -----------------------------------------------------------------------------

INSERT INTO clientes (id, tipo_pessoa, email, ativo, criado_em, atualizado_em) VALUES
    (UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000001'), 'FISICA', 'joao.silva@email.com',      true, NOW(), NOW()),
    (UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000002'), 'FISICA', 'maria.santos@email.com',    true, NOW(), NOW()),
    (UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000003'), 'FISICA', 'carlos.oliveira@email.com', true, NOW(), NOW());

INSERT INTO clientes_pf (id, nome, cpf, rg, data_nascimento) VALUES
    (UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000001'), 'João Silva',      '52998224725', '345678901',  '1985-03-15'),
    (UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000002'), 'Maria Santos',    '87748248800', 'MG1234567',  '1990-07-22'),
    (UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000003'), 'Carlos Oliveira', '34428781060', 'SP9876543',  '1978-11-30');

-- -----------------------------------------------------------------------------
-- Clientes PJ
-- -----------------------------------------------------------------------------

INSERT INTO clientes (id, tipo_pessoa, email, ativo, criado_em, atualizado_em) VALUES
    (UUID_TO_BIN('bbbbbbbb-0002-0000-0000-000000000001'), 'JURIDICA', 'contato@techsolutions.com.br',    true, NOW(), NOW()),
    (UUID_TO_BIN('bbbbbbbb-0002-0000-0000-000000000002'), 'JURIDICA', 'comercial@comercioglobal.com.br', true, NOW(), NOW());

INSERT INTO clientes_pj (id, cnpj, razao_social, inscricao_estadual, data_criacao) VALUES
    (UUID_TO_BIN('bbbbbbbb-0002-0000-0000-000000000001'), '11222333000181', 'Tech Solutions Ltda',  '123456789112', '2010-05-12'),
    (UUID_TO_BIN('bbbbbbbb-0002-0000-0000-000000000002'), '45678901000195', 'Comércio Global S.A.', '987654321000', '2005-08-20');

-- -----------------------------------------------------------------------------
-- Endereços
-- -----------------------------------------------------------------------------

INSERT INTO enderecos (id, logradouro, numero, cep, bairro, telefone, cidade, estado, endereco_principal, complemento, cliente_id) VALUES
    -- João Silva — 1 endereço
    (UUID_TO_BIN('cccccccc-0003-0000-0000-000000000001'), 'Rua das Flores',        '123',  '01310-100', 'Centro',          '(11) 99999-1111', 'São Paulo',      'SP', true,  'Apto 45',      UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000001')),

    -- Maria Santos — 1 endereço
    (UUID_TO_BIN('cccccccc-0003-0000-0000-000000000002'), 'Av. Afonso Pena',       '456',  '30130-005', 'Centro',          '(31) 98888-2222', 'Belo Horizonte', 'MG', true,  NULL,           UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000002')),

    -- Carlos Oliveira — 2 endereços
    (UUID_TO_BIN('cccccccc-0003-0000-0000-000000000003'), 'Rua XV de Novembro',    '789',  '80020-310', 'Centro',          '(41) 97777-3333', 'Curitiba',       'PR', true,  'Sala 12',      UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000003')),
    (UUID_TO_BIN('cccccccc-0003-0000-0000-000000000004'), 'Rua Marechal Deodoro',  '100',  '80010-010', 'Centro Histórico','(41) 96666-3334', 'Curitiba',       'PR', false, NULL,           UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000003')),

    -- Tech Solutions Ltda — 1 endereço
    (UUID_TO_BIN('cccccccc-0003-0000-0000-000000000005'), 'Av. Paulista',          '1000', '01310-100', 'Bela Vista',      '(11) 3333-4444',  'São Paulo',      'SP', true,  '10º andar',    UUID_TO_BIN('bbbbbbbb-0002-0000-0000-000000000001')),

    -- Comércio Global S.A. — 2 endereços
    (UUID_TO_BIN('cccccccc-0003-0000-0000-000000000006'), 'Rua dos Andradas',      '500',  '90020-004', 'Centro',          '(51) 3232-5555',  'Porto Alegre',   'RS', true,  'Conj. 305',    UUID_TO_BIN('bbbbbbbb-0002-0000-0000-000000000002')),
    (UUID_TO_BIN('cccccccc-0003-0000-0000-000000000007'), 'Av. Borges de Medeiros','1600', '90110-150', 'Praia de Belas',  '(51) 3232-5556',  'Porto Alegre',   'RS', false, NULL,           UUID_TO_BIN('bbbbbbbb-0002-0000-0000-000000000002'));
