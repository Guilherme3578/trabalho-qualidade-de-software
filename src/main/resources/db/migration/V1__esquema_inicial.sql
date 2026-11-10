CREATE TABLE professores (
    id           VARCHAR(36)  NOT NULL,
    nome         VARCHAR(120) NOT NULL,
    email        VARCHAR(160) NOT NULL,
    departamento VARCHAR(80),
    ativo        BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_professores PRIMARY KEY (id),
    CONSTRAINT uk_professores_email UNIQUE (email)
);

CREATE TABLE professor_competencias (
    professor_id VARCHAR(36) NOT NULL,
    competencia  VARCHAR(80) NOT NULL,
    CONSTRAINT pk_professor_competencias PRIMARY KEY (professor_id, competencia),
    CONSTRAINT fk_competencias_professor FOREIGN KEY (professor_id) REFERENCES professores (id) ON DELETE CASCADE
);

CREATE INDEX idx_competencias_competencia ON professor_competencias (competencia);

CREATE TABLE salas (
    id          VARCHAR(36)  NOT NULL,
    codigo      VARCHAR(20)  NOT NULL,
    nome        VARCHAR(120) NOT NULL,
    tipo        VARCHAR(20)  NOT NULL,
    capacidade  INT          NOT NULL,
    localizacao VARCHAR(120) NOT NULL,
    restrita    BOOLEAN      NOT NULL DEFAULT FALSE,
    ativa       BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_salas PRIMARY KEY (id),
    CONSTRAINT uk_salas_codigo UNIQUE (codigo),
    CONSTRAINT ck_salas_tipo CHECK (tipo IN ('SALA_AULA', 'LABORATORIO', 'AUDITORIO')),
    CONSTRAINT ck_salas_capacidade CHECK (capacidade BETWEEN 1 AND 1000)
);

CREATE INDEX idx_salas_tipo ON salas (tipo);

CREATE TABLE materiais (
    id               VARCHAR(36)  NOT NULL,
    nome             VARCHAR(120) NOT NULL,
    categoria        VARCHAR(60)  NOT NULL,
    patrimonio       VARCHAR(40),
    quantidade_total INT          NOT NULL,
    localizacao      VARCHAR(120),
    restrito         BOOLEAN      NOT NULL DEFAULT FALSE,
    ativo            BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_materiais PRIMARY KEY (id),
    CONSTRAINT uk_materiais_patrimonio UNIQUE (patrimonio),
    CONSTRAINT ck_materiais_quantidade CHECK (quantidade_total BETWEEN 0 AND 10000)
);

CREATE INDEX idx_materiais_categoria ON materiais (categoria);

CREATE TABLE usuarios (
    id           VARCHAR(36)  NOT NULL,
    nome         VARCHAR(120) NOT NULL,
    email        VARCHAR(160) NOT NULL,
    senha_hash   VARCHAR(100) NOT NULL,
    perfil       VARCHAR(20)  NOT NULL,
    professor_id VARCHAR(36),
    ativo        BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_em    DATETIME(6)  NOT NULL,
    CONSTRAINT pk_usuarios PRIMARY KEY (id),
    CONSTRAINT uk_usuarios_email UNIQUE (email),
    CONSTRAINT fk_usuarios_professor FOREIGN KEY (professor_id) REFERENCES professores (id),
    CONSTRAINT ck_usuarios_perfil CHECK (perfil IN ('PROFESSOR', 'COORDENADOR', 'RESPONSAVEL', 'ADMIN'))
);

CREATE INDEX idx_usuarios_perfil ON usuarios (perfil, ativo);

CREATE TABLE reservas (
    id                VARCHAR(36)  NOT NULL,
    sala_id           VARCHAR(36)  NOT NULL,
    professor_id      VARCHAR(36)  NOT NULL,
    solicitante_id    VARCHAR(36)  NOT NULL,
    disciplina        VARCHAR(120) NOT NULL,
    inicio            DATETIME(6)  NOT NULL,
    fim               DATETIME(6)  NOT NULL,
    quantidade_alunos INT          NOT NULL,
    status            VARCHAR(20)  NOT NULL,
    observacao        VARCHAR(500),
    versao            BIGINT       NOT NULL,
    criado_em         DATETIME(6)  NOT NULL,
    atualizado_em     DATETIME(6)  NOT NULL,
    CONSTRAINT pk_reservas PRIMARY KEY (id),
    CONSTRAINT fk_reservas_sala FOREIGN KEY (sala_id) REFERENCES salas (id),
    CONSTRAINT fk_reservas_professor FOREIGN KEY (professor_id) REFERENCES professores (id),
    CONSTRAINT fk_reservas_solicitante FOREIGN KEY (solicitante_id) REFERENCES usuarios (id),
    CONSTRAINT ck_reservas_periodo CHECK (fim > inicio),
    CONSTRAINT ck_reservas_alunos CHECK (quantidade_alunos BETWEEN 1 AND 1000),
    CONSTRAINT ck_reservas_status CHECK (status IN ('SOLICITADA', 'APROVADA', 'EM_USO', 'CONCLUIDA', 'REJEITADA',
                                                    'CANCELADA', 'NAO_COMPARECEU'))
);

CREATE INDEX idx_reservas_sala_periodo ON reservas (sala_id, status, inicio);
CREATE INDEX idx_reservas_professor_periodo ON reservas (professor_id, status, inicio);
CREATE INDEX idx_reservas_solicitante ON reservas (solicitante_id);
CREATE INDEX idx_reservas_status_inicio ON reservas (status, inicio);

CREATE TABLE reserva_itens (
    reserva_id  VARCHAR(36) NOT NULL,
    material_id VARCHAR(36) NOT NULL,
    quantidade  INT         NOT NULL,
    CONSTRAINT pk_reserva_itens PRIMARY KEY (reserva_id, material_id),
    CONSTRAINT fk_itens_reserva FOREIGN KEY (reserva_id) REFERENCES reservas (id) ON DELETE CASCADE,
    CONSTRAINT fk_itens_material FOREIGN KEY (material_id) REFERENCES materiais (id),
    CONSTRAINT ck_itens_quantidade CHECK (quantidade BETWEEN 1 AND 500)
);

CREATE INDEX idx_reserva_itens_material ON reserva_itens (material_id);

CREATE TABLE reserva_motivos_aprovacao (
    reserva_id VARCHAR(36)  NOT NULL,
    ordem      INT          NOT NULL,
    motivo     VARCHAR(255) NOT NULL,
    CONSTRAINT pk_reserva_motivos PRIMARY KEY (reserva_id, ordem),
    CONSTRAINT fk_motivos_reserva FOREIGN KEY (reserva_id) REFERENCES reservas (id) ON DELETE CASCADE
);

CREATE TABLE reserva_movimentacoes (
    id                VARCHAR(36)  NOT NULL,
    reserva_id        VARCHAR(36)  NOT NULL,
    material_id       VARCHAR(36)  NOT NULL,
    tipo              VARCHAR(20)  NOT NULL,
    quantidade        INT          NOT NULL,
    registrado_por_id VARCHAR(36)  NOT NULL,
    registrado_em     DATETIME(6)  NOT NULL,
    observacao        VARCHAR(255),
    CONSTRAINT pk_reserva_movimentacoes PRIMARY KEY (id),
    CONSTRAINT fk_movimentacoes_reserva FOREIGN KEY (reserva_id) REFERENCES reservas (id) ON DELETE CASCADE,
    CONSTRAINT fk_movimentacoes_material FOREIGN KEY (material_id) REFERENCES materiais (id),
    CONSTRAINT fk_movimentacoes_usuario FOREIGN KEY (registrado_por_id) REFERENCES usuarios (id),
    CONSTRAINT ck_movimentacoes_tipo CHECK (tipo IN ('RETIRADA', 'DEVOLUCAO')),
    CONSTRAINT ck_movimentacoes_quantidade CHECK (quantidade > 0)
);

CREATE TABLE bloqueios (
    id            VARCHAR(36)  NOT NULL,
    tipo_recurso  VARCHAR(20)  NOT NULL,
    sala_id       VARCHAR(36),
    professor_id  VARCHAR(36),
    material_id   VARCHAR(36),
    inicio        DATETIME(6)  NOT NULL,
    fim           DATETIME(6)  NOT NULL,
    motivo        VARCHAR(255) NOT NULL,
    criado_por_id VARCHAR(36)  NOT NULL,
    criado_em     DATETIME(6)  NOT NULL,
    CONSTRAINT pk_bloqueios PRIMARY KEY (id),
    CONSTRAINT fk_bloqueios_sala FOREIGN KEY (sala_id) REFERENCES salas (id),
    CONSTRAINT fk_bloqueios_professor FOREIGN KEY (professor_id) REFERENCES professores (id),
    CONSTRAINT fk_bloqueios_material FOREIGN KEY (material_id) REFERENCES materiais (id),
    CONSTRAINT fk_bloqueios_usuario FOREIGN KEY (criado_por_id) REFERENCES usuarios (id),
    CONSTRAINT ck_bloqueios_periodo CHECK (fim > inicio),
    CONSTRAINT ck_bloqueios_recurso CHECK (
        (tipo_recurso = 'SALA' AND sala_id IS NOT NULL AND professor_id IS NULL AND material_id IS NULL)
        OR (tipo_recurso = 'PROFESSOR' AND professor_id IS NOT NULL AND sala_id IS NULL AND material_id IS NULL)
        OR (tipo_recurso = 'MATERIAL' AND material_id IS NOT NULL AND sala_id IS NULL AND professor_id IS NULL))
);

CREATE INDEX idx_bloqueios_sala_periodo ON bloqueios (sala_id, inicio);
CREATE INDEX idx_bloqueios_professor_periodo ON bloqueios (professor_id, inicio);
CREATE INDEX idx_bloqueios_material_periodo ON bloqueios (material_id, inicio);

CREATE TABLE notificacoes (
    id                VARCHAR(36)  NOT NULL,
    destinatario_id   VARCHAR(36)  NOT NULL,
    destinatario_email VARCHAR(160) NOT NULL,
    tipo              VARCHAR(40)  NOT NULL,
    mensagem          TEXT         NOT NULL,
    reserva_id        VARCHAR(36),
    status            VARCHAR(20),
    erro              TEXT,
    lida              BOOLEAN      NOT NULL DEFAULT FALSE,
    criado_em         DATETIME(6)  NOT NULL,
    CONSTRAINT pk_notificacoes PRIMARY KEY (id),
    CONSTRAINT fk_notificacoes_usuario FOREIGN KEY (destinatario_id) REFERENCES usuarios (id),
    CONSTRAINT fk_notificacoes_reserva FOREIGN KEY (reserva_id) REFERENCES reservas (id) ON DELETE SET NULL
);

CREATE INDEX idx_notificacoes_destinatario ON notificacoes (destinatario_id, criado_em);

CREATE TABLE conflitos_evitados (
    id             BIGINT      NOT NULL AUTO_INCREMENT,
    tipo           VARCHAR(40) NOT NULL,
    recurso_tipo   VARCHAR(20) NOT NULL,
    recurso_id     VARCHAR(36) NOT NULL,
    inicio         DATETIME(6) NOT NULL,
    fim            DATETIME(6) NOT NULL,
    solicitante_id VARCHAR(36) NOT NULL,
    registrado_em  DATETIME(6) NOT NULL,
    CONSTRAINT pk_conflitos_evitados PRIMARY KEY (id),
    CONSTRAINT fk_conflitos_solicitante FOREIGN KEY (solicitante_id) REFERENCES usuarios (id)
);

CREATE INDEX idx_conflitos_registrado_em ON conflitos_evitados (registrado_em);

CREATE TABLE auditoria (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    data_hora       DATETIME(6)  NOT NULL,
    usuario_id      VARCHAR(36),
    usuario_nome    VARCHAR(120) NOT NULL,
    acao            VARCHAR(30)  NOT NULL,
    entidade        VARCHAR(40)  NOT NULL,
    entidade_id     VARCHAR(160),
    estado_anterior TEXT,
    estado_novo     TEXT,
    detalhes        TEXT,
    correlation_id  VARCHAR(64),
    CONSTRAINT pk_auditoria PRIMARY KEY (id)
);

CREATE INDEX idx_auditoria_entidade ON auditoria (entidade, entidade_id, data_hora);
CREATE INDEX idx_auditoria_data_hora ON auditoria (data_hora);
