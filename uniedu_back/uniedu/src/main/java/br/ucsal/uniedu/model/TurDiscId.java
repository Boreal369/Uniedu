package br.ucsal.uniedu.model;

import jakarta.persistence.*;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TurDiscId implements Serializable {

    @Column(name = "cod_turma")
    private Integer codTurma;

    @Column(name = "cod_disciplina")
    private Integer codDisciplina;

    public TurDiscId() {
    }

    public TurDiscId(Integer codTurma, Integer codDisciplina) {
        this.codTurma = codTurma;
        this.codDisciplina = codDisciplina;
    }

    public Integer getCodTurma() {
        return codTurma;
    }

    public void setCodTurma(Integer codTurma) {
        this.codTurma = codTurma;
    }

    public Integer getCodDisciplina() {
        return codDisciplina;
    }

    public void setCodDisciplina(Integer codDisciplina) {
        this.codDisciplina = codDisciplina;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TurDiscId turDiscId = (TurDiscId) o;
        return Objects.equals(codTurma, turDiscId.codTurma) && Objects.equals(codDisciplina, turDiscId.codDisciplina);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codTurma, codDisciplina);
    }
}