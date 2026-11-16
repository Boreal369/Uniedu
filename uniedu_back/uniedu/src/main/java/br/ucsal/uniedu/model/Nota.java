package br.ucsal.uniedu.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "nota")
public class Nota {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "cod_nota")
   private Integer codNota;

    @Column(name = "valor", nullable = false, precision = 4, scale = 2)
    private BigDecimal valor;

    @Column(name = "unidade", nullable = false)
    private Integer unidade;

    @Column(name = "data_lancamento", nullable = false)
    private LocalDate dataLancamento = LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "cod_aluno", nullable = false)
    private Aluno aluno;

    @ManyToOne
    @JoinColumn(name = "cod_disciplina", nullable = false)
    private Disciplina disciplina;

    public Nota() {
    }


    public Integer getCodNota() {
        return codNota;
    }

    public void setCodNota(Integer codNota) {
        this.codNota = codNota;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Integer getUnidade() {
        return unidade;
    }

    public void setUnidade(Integer unidade) {
        this.unidade = unidade;
    }

    public LocalDate getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(LocalDate dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }
}