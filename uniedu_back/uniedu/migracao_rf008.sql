-- RF008
ALTER TABLE aviso ADD COLUMN destino VARCHAR(20) NOT NULL DEFAULT 'TODOS';
ALTER TABLE aviso ADD COLUMN cod_turma INT;
ALTER TABLE aviso ADD CONSTRAINT chk_aviso_destino CHECK (destino IN ('TODOS', 'ALUNOS', 'PROFESSORES'));
ALTER TABLE aviso ADD CONSTRAINT fk_aviso_turma FOREIGN KEY (cod_turma) REFERENCES turma (cod_turma) ON DELETE CASCADE;
CREATE INDEX idx_aviso_turma ON aviso (cod_turma);
