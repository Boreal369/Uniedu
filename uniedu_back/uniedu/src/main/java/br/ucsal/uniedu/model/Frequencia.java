package br.ucsal.uniedu.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "frequencia")
public class Frequencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_frequencia")
    private Integer codFrequencia;

    @Column(name = "data_aula", nullable = false)
    private LocalDate dataAula;

    @Column(name = "presenca", nullable = false)
    private Boolean presenca;

    @ManyToOne
    @JoinColumn(name = "cod_disciplina", nullable = false)
    private Disciplina disciplina;

    @ManyToOne
    @JoinColumn(name = "cod_aluno", nullable = false)
    private Aluno aluno;

    public Frequencia() {
    }


    public Integer getCodFrequencia() {
        return codFrequencia;
    }

    public void setCodFrequencia(Integer codFrequencia) {
        this.codFrequencia = codFrequencia;
    }

    public LocalDate getDataAula() {
        return dataAula;
    }

    public void setDataAula(LocalDate dataAula) {
        this.dataAula = dataAula;
    }

    public Boolean getPresenca() {
        return presenca;
    }

    public void setPresenca(Boolean presenca) {
        this.presenca = presenca;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }
}