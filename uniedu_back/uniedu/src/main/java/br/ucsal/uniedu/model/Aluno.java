package br.ucsal.uniedu.model;

import jakarta.persistence.*;


@Entity
@Table(name = "aluno")
public class Aluno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_aluno")
    private Integer codAluno;

    @Column(name = "matricula", nullable = false, unique = true)
    private Long matricula;

    @ManyToOne
    @JoinColumn(name = "cod_turma", nullable = false)
    private Turma turma;

    @OneToOne
    @JoinColumn(name = "cod_usuario", nullable = false)
    private Usuario usuario;

    public Aluno() {
    }

    public Integer getCodAluno() {
        return codAluno;
    }

    public void setCodAluno(Integer codAluno) {
        this.codAluno = codAluno;
    }

    public Long getMatricula() {
        return matricula;
    }

    public void setMatricula(Long matricula) {
        this.matricula = matricula;
    }

    public Turma getTurma() {
        return turma;
    }

    public void setTurma(Turma turma) {
        this.turma = turma;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}