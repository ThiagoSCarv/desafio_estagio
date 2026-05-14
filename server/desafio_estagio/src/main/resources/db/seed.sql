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

-- -----------------------------------------------------------------------------
-- Clientes PF adicionais (04–15)
-- -----------------------------------------------------------------------------

INSERT INTO clientes (id, tipo_pessoa, email, ativo, criado_em, atualizado_em) VALUES
    (UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000004'), 'FISICA', 'ana.beatriz@email.com',      true, NOW(), NOW()),
    (UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000005'), 'FISICA', 'pedro.lima@email.com',        true, NOW(), NOW()),
    (UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000006'), 'FISICA', 'fernanda.souza@email.com',    true, NOW(), NOW()),
    (UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000007'), 'FISICA', 'roberto.pereira@email.com',   true, NOW(), NOW()),
    (UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000008'), 'FISICA', 'juliana.rocha@email.com',     true, NOW(), NOW()),
    (UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000009'), 'FISICA', 'marcelo.alves@email.com',     true, NOW(), NOW()),
    (UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000010'), 'FISICA', 'patricia.gomes@email.com',    true, NOW(), NOW()),
    (UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000011'), 'FISICA', 'ricardo.carvalho@email.com',  true, NOW(), NOW()),
    (UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000012'), 'FISICA', 'sandra.ferreira@email.com',   true, NOW(), NOW()),
    (UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000013'), 'FISICA', 'bruno.martins@email.com',     true, NOW(), NOW()),
    (UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000014'), 'FISICA', 'camila.ribeiro@email.com',    true, NOW(), NOW()),
    (UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000015'), 'FISICA', 'daniel.barbosa@email.com',    true, NOW(), NOW());

INSERT INTO clientes_pf (id, nome, cpf, rg, data_nascimento) VALUES
    (UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000004'), 'Ana Beatriz Costa',   '61494100053', 'BA1234567', '1992-04-18'),
    (UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000005'), 'Pedro Henrique Lima', '42315677866', 'PE9876543', '1988-09-05'),
    (UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000006'), 'Fernanda Souza',      '78945612319', 'RJ2345678', '1995-01-22'),
    (UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000007'), 'Roberto Pereira',     '32165498791', 'GO8765432', '1975-07-14'),
    (UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000008'), 'Juliana Rocha',       '65432100010', 'RS3456789', '1983-12-03'),
    (UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000009'), 'Marcelo Alves',       '14725836982', 'CE7654321', '1997-06-28'),
    (UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000010'), 'Patricia Gomes',      '96385274128', 'AM4567890', '1970-02-11'),
    (UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000011'), 'Ricardo Carvalho',    '25874196382', 'PA6543210', '1986-10-17'),
    (UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000012'), 'Sandra Ferreira',     '74185296355', 'SC5432109', '1993-08-25'),
    (UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000013'), 'Bruno Martins',       '15935748606', 'DF2109876', '1980-03-07'),
    (UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000014'), 'Camila Ribeiro',      '48615935734', 'TO1098765', '1998-11-30'),
    (UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000015'), 'Daniel Barbosa',      '35748615916', 'MT0987654', '1973-05-19');

-- -----------------------------------------------------------------------------
-- Clientes PJ adicionais (03–07)
-- -----------------------------------------------------------------------------

INSERT INTO clientes (id, tipo_pessoa, email, ativo, criado_em, atualizado_em) VALUES
    (UUID_TO_BIN('bbbbbbbb-0002-0000-0000-000000000003'), 'JURIDICA', 'contato@solucoesdigitais.com.br',   true, NOW(), NOW()),
    (UUID_TO_BIN('bbbbbbbb-0002-0000-0000-000000000004'), 'JURIDICA', 'contato@distribuidoranorte.com.br', true, NOW(), NOW()),
    (UUID_TO_BIN('bbbbbbbb-0002-0000-0000-000000000005'), 'JURIDICA', 'contato@construtoraalfa.com.br',    true, NOW(), NOW()),
    (UUID_TO_BIN('bbbbbbbb-0002-0000-0000-000000000006'), 'JURIDICA', 'farmacia@bemestarsaude.com.br',     true, NOW(), NOW()),
    (UUID_TO_BIN('bbbbbbbb-0002-0000-0000-000000000007'), 'JURIDICA', 'contato@transportadorasul.com.br',  true, NOW(), NOW());

INSERT INTO clientes_pj (id, cnpj, razao_social, inscricao_estadual, data_criacao) VALUES
    (UUID_TO_BIN('bbbbbbbb-0002-0000-0000-000000000003'), '33000167000101', 'Soluções Digitais ME',     '111222333444', '2015-03-10'),
    (UUID_TO_BIN('bbbbbbbb-0002-0000-0000-000000000004'), '22222222000191', 'Distribuidora Norte Ltda', '555666777888', '2008-07-22'),
    (UUID_TO_BIN('bbbbbbbb-0002-0000-0000-000000000005'), '55000000000183', 'Construtora Alfa S.A.',    '999000111222', '2000-01-15'),
    (UUID_TO_BIN('bbbbbbbb-0002-0000-0000-000000000006'), '77777777000191', 'Farmácia Bem Estar ME',    '333444555666', '2018-09-03'),
    (UUID_TO_BIN('bbbbbbbb-0002-0000-0000-000000000007'), '88000000000213', 'Transportadora Sul Ltda',  '777888999000', '2012-04-28');

-- -----------------------------------------------------------------------------
-- Endereços dos novos clientes (08–24)
-- -----------------------------------------------------------------------------

INSERT INTO enderecos (id, logradouro, numero, cep, bairro, telefone, cidade, estado, endereco_principal, complemento, cliente_id) VALUES
    (UUID_TO_BIN('cccccccc-0003-0000-0000-000000000008'), 'Av. Tancredo Neves',     '1500', '41820-020', 'Caminho das Árvores', '(71) 99111-2222', 'Salvador',      'BA', true, 'Torre Sul', UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000004')),
    (UUID_TO_BIN('cccccccc-0003-0000-0000-000000000009'), 'Rua do Bom Jesus',       '320',  '50030-170', 'Recife Antigo',       '(81) 98222-3333', 'Recife',        'PE', true, NULL,        UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000005')),
    (UUID_TO_BIN('cccccccc-0003-0000-0000-000000000010'), 'Av. Beira Mar',          '2200', '60165-121', 'Meireles',            '(85) 97333-4444', 'Fortaleza',     'CE', true, 'Apto 801',  UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000006')),
    (UUID_TO_BIN('cccccccc-0003-0000-0000-000000000011'), 'Av. Djalma Batista',     '800',  '69050-010', 'Chapada',             '(92) 96444-5555', 'Manaus',        'AM', true, NULL,        UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000007')),
    (UUID_TO_BIN('cccccccc-0003-0000-0000-000000000012'), 'Av. Nazaré',             '100',  '66035-170', 'Nazaré',              '(91) 95555-6666', 'Belém',         'PA', true, 'Casa',      UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000008')),
    (UUID_TO_BIN('cccccccc-0003-0000-0000-000000000013'), 'Rua 3',                  '450',  '74110-020', 'Setor Oeste',         '(62) 94666-7777', 'Goiânia',       'GO', true, NULL,        UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000009')),
    (UUID_TO_BIN('cccccccc-0003-0000-0000-000000000014'), 'SQN 304',                '10',   '70736-040', 'Asa Norte',           '(61) 93777-8888', 'Brasília',      'DF', true, 'Bloco B',   UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000010')),
    (UUID_TO_BIN('cccccccc-0003-0000-0000-000000000015'), 'Rua Felipe Schmidt',     '780',  '88010-001', 'Centro',              '(48) 92888-9999', 'Florianópolis', 'SC', true, NULL,        UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000011')),
    (UUID_TO_BIN('cccccccc-0003-0000-0000-000000000016'), 'Av. Prudente de Morais', '600',  '59020-400', 'Petrópolis',          '(84) 91999-0000', 'Natal',         'RN', true, NULL,        UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000012')),
    (UUID_TO_BIN('cccccccc-0003-0000-0000-000000000017'), 'Av. Epitácio Pessoa',    '1200', '58030-000', 'Tambaú',              '(83) 90000-1111', 'João Pessoa',   'PB', true, 'Sala 3',    UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000013')),
    (UUID_TO_BIN('cccccccc-0003-0000-0000-000000000018'), 'Av. Fernandes Lima',     '900',  '57036-900', 'Farol',               '(82) 99000-2222', 'Maceió',        'AL', true, NULL,        UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000014')),
    (UUID_TO_BIN('cccccccc-0003-0000-0000-000000000019'), 'Av. Frei Serafim',       '2280', '64001-320', 'Centro',              '(86) 98100-3333', 'Teresina',      'PI', true, NULL,        UUID_TO_BIN('aaaaaaaa-0001-0000-0000-000000000015')),
    (UUID_TO_BIN('cccccccc-0003-0000-0000-000000000020'), 'Av. Afonso Pena',        '7000', '79002-071', 'Centro',              '(67) 97200-4444', 'Campo Grande',  'MS', true, '5º andar',  UUID_TO_BIN('bbbbbbbb-0002-0000-0000-000000000003')),
    (UUID_TO_BIN('cccccccc-0003-0000-0000-000000000021'), 'Av. Isaac Póvoas',       '450',  '78005-150', 'Centro Norte',        '(65) 96300-5555', 'Cuiabá',        'MT', true, NULL,        UUID_TO_BIN('bbbbbbbb-0002-0000-0000-000000000004')),
    (UUID_TO_BIN('cccccccc-0003-0000-0000-000000000022'), 'Av. Lauro Sodré',        '1000', '76801-090', 'São Cristóvão',       '(69) 95400-6666', 'Porto Velho',   'RO', true, 'Galpão 2',  UUID_TO_BIN('bbbbbbbb-0002-0000-0000-000000000005')),
    (UUID_TO_BIN('cccccccc-0003-0000-0000-000000000023'), 'Av. FAB',                '200',  '68900-073', 'Central',             '(96) 94500-7777', 'Macapá',        'AP', true, NULL,        UUID_TO_BIN('bbbbbbbb-0002-0000-0000-000000000006')),
    (UUID_TO_BIN('cccccccc-0003-0000-0000-000000000024'), 'Av. Ceará',              '3200', '69900-640', 'Bosque',              '(68) 93600-8888', 'Rio Branco',    'AC', true, NULL,        UUID_TO_BIN('bbbbbbbb-0002-0000-0000-000000000007'));
