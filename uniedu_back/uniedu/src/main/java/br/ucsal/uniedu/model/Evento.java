package br.ucsal.uniedu.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "evento")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_evento", nullable = false)
    private Integer codEvento;

    @Column(name = "descricao", nullable = false,  length = 100)
    private String descricao;

    @Column(name = "data_evento", nullable = false)
    private LocalDate dataEvento;

    @ManyToOne
    @JoinColumn(name = "cod_prof", nullable = false)
    private Professor professor;

    @ManyToOne
    @JoinColumn(name = "cod_turma", nullable = false)
    private Turma turma;

    public Evento() {
    }

    public Integer getCodEvento() {
        return codEvento;
    }

    public void setCodEvento(Integer codEvento) {
        this.codEvento = codEvento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataEvento() {
        return dataEvento;
    }

    public void setDataEvento(LocalDate dataEvento) {
        this.dataEvento = dataEvento;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public Turma getTurma() {
        return turma;
    }

    public void setTurma(Turma turma) {
        this.turma = turma;
    }
}