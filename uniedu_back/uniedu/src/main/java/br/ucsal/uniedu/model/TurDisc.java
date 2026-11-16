package br.ucsal.uniedu.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tur_disc")
public class TurDisc {

    @EmbeddedId
    private TurDiscId id = new TurDiscId();

    @ManyToOne
    @MapsId("codTurma")
    @JoinColumn(name = "cod_turma", nullable = false)
    private Turma turma;

    @ManyToOne
    @MapsId("codDisciplina")
    @JoinColumn(name = "cod_disciplina", nullable = false)
    private Disciplina disciplina;

    @ManyToOne
    @JoinColumn(name = "cod_prof", nullable = false)
    private Professor professor;

    public TurDisc() {
    }


    public TurDiscId getId() {
        return id;
    }

    public void setId(TurDiscId id) {
        this.id = id;
    }

    public Turma getTurma() {
        return turma;
    }

    public void setTurma(Turma turma) {
        this.turma = turma;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }
}