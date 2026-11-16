CREATE TABLE usuario (
    cod_usuario INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    login VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(100) NOT NULL,
    perfil VARCHAR(20) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_usuario_perfil CHECK (perfil IN ('ADMIN', 'PROFESSOR', 'ALUNO'))
);

CREATE TABLE turma (
    cod_turma INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome_turma VARCHAR(100) NOT NULL,
    ano_letivo INT NOT NULL,
    CONSTRAINT chk_turma_ano CHECK (ano_letivo >= 2000)
);

CREATE TABLE disciplina (
    cod_disciplina INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);

CREATE TABLE professor (
    cod_prof INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cod_usuario INT NOT NULL UNIQUE,
    CONSTRAINT fk_prof_usuario FOREIGN KEY (cod_usuario)
        REFERENCES usuario (cod_usuario)
        ON DELETE CASCADE
);

CREATE TABLE aluno (
    cod_aluno INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    matricula BIGINT NOT NULL UNIQUE,
    cod_turma INT NOT NULL,
    cod_usuario INT NOT NULL UNIQUE,
    CONSTRAINT fk_aluno_turma FOREIGN KEY (cod_turma)
        REFERENCES turma (cod_turma)
        ON DELETE RESTRICT,
    CONSTRAINT fk_aluno_usuario FOREIGN KEY (cod_usuario)
        REFERENCES usuario (cod_usuario)
        ON DELETE CASCADE
);

CREATE TABLE tur_disc (
    cod_turma INT NOT NULL,
    cod_disciplina INT NOT NULL,
    cod_prof INT NOT NULL,
    PRIMARY KEY (cod_turma, cod_disciplina),
    CONSTRAINT fk_tur_disc_turma FOREIGN KEY (cod_turma)
        REFERENCES turma (cod_turma)
        ON DELETE RESTRICT,
    CONSTRAINT fk_tur_disc_disc FOREIGN KEY (cod_disciplina)
        REFERENCES disciplina (cod_disciplina)
        ON DELETE RESTRICT,
    CONSTRAINT fk_tur_disc_prof FOREIGN KEY (cod_prof)
        REFERENCES professor (cod_prof)
        ON DELETE RESTRICT
);

CREATE TABLE evento (
    cod_evento INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    descricao VARCHAR(100) NOT NULL,
    data_evento DATE NOT NULL,
    cod_prof INT NOT NULL,
    cod_turma INT NOT NULL,
    CONSTRAINT fk_evento_prof FOREIGN KEY (cod_prof)
        REFERENCES professor (cod_prof)
        ON DELETE RESTRICT,
    CONSTRAINT fk_evento_turma FOREIGN KEY (cod_turma)
        REFERENCES turma (cod_turma)
        ON DELETE CASCADE
);

CREATE TABLE material (
    cod_material INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    tipo VARCHAR(100) NOT NULL,
    url_conteudo TEXT NOT NULL,
    data_postagem DATE NOT NULL DEFAULT CURRENT_DATE,
    cod_disciplina INT NOT NULL,
    cod_prof INT NOT NULL,
    CONSTRAINT fk_material_disc FOREIGN KEY (cod_disciplina)
        REFERENCES disciplina (cod_disciplina)
        ON DELETE RESTRICT,
    CONSTRAINT fk_material_prof FOREIGN KEY (cod_prof)
        REFERENCES professor (cod_prof)
        ON DELETE RESTRICT
);

CREATE TABLE frequencia (
    cod_frequencia INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    data_aula DATE NOT NULL,
    presenca BOOLEAN NOT NULL,
    cod_disciplina INT NOT NULL,
    cod_aluno INT NOT NULL,
    CONSTRAINT uq_frequencia_aluno_disc_data UNIQUE (cod_aluno, cod_disciplina, data_aula),
    CONSTRAINT fk_frequencia_disc FOREIGN KEY (cod_disciplina)
        REFERENCES disciplina (cod_disciplina)
        ON DELETE RESTRICT,
    CONSTRAINT fk_frequencia_aluno FOREIGN KEY (cod_aluno)
        REFERENCES aluno (cod_aluno)
        ON DELETE RESTRICT
);

CREATE TABLE nota (
    cod_nota INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    valor NUMERIC(4,2) NOT NULL,
    unidade INT NOT NULL,
    data_lancamento DATE NOT NULL DEFAULT CURRENT_DATE,
    cod_aluno INT NOT NULL,
    cod_disciplina INT NOT NULL,
    CONSTRAINT chk_nota_valor CHECK (valor >= 0 AND valor <= 10),
    CONSTRAINT chk_nota_unidade CHECK (unidade > 0),
    CONSTRAINT uq_nota_aluno_disc_unidade UNIQUE (cod_aluno, cod_disciplina, unidade),
    CONSTRAINT fk_nota_aluno FOREIGN KEY (cod_aluno)
        REFERENCES aluno (cod_aluno)
        ON DELETE RESTRICT,
    CONSTRAINT fk_nota_disc FOREIGN KEY (cod_disciplina)
        REFERENCES disciplina (cod_disciplina)
        ON DELETE RESTRICT
);

CREATE TABLE documento (
    cod_documento INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    url_arquivo TEXT NOT NULL,
    data_envio DATE NOT NULL DEFAULT CURRENT_DATE,
    cod_aluno INT NOT NULL,
    CONSTRAINT fk_documento_aluno FOREIGN KEY (cod_aluno)
        REFERENCES aluno (cod_aluno)
        ON DELETE RESTRICT
);

CREATE TABLE aviso (
    cod_aviso INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    mensagem TEXT NOT NULL,
    data_publi DATE NOT NULL DEFAULT CURRENT_DATE,
    cod_usuario INT NOT NULL,
    CONSTRAINT fk_aviso_usuario FOREIGN KEY (cod_usuario)
        REFERENCES usuario (cod_usuario)
        ON DELETE RESTRICT
);

CREATE INDEX idx_aluno_turma ON aluno (cod_turma);
CREATE INDEX idx_tur_disc_prof ON tur_disc (cod_prof);
CREATE INDEX idx_nota_disciplina ON nota (cod_disciplina);
CREATE INDEX idx_frequencia_disciplina_data ON frequencia (cod_disciplina, data_aula);
CREATE INDEX idx_material_disciplina ON material (cod_disciplina);
CREATE INDEX idx_evento_turma ON evento (cod_turma);