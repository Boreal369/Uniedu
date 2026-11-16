package br.ucsal.uniedu.model;

import jakarta.persistence.*;

@Entity
@Table(name = "turma")
public class Turma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_turma")
    private Integer codTurma;

    @Column(name = "nome_turma", nullable = false, length = 100)
    private String nomeTurma;

    @Column(name = "ano_letivo", nullable = false)
    private Integer anoLetivo;

    public Turma() {
    }

    public Integer getCodTurma() {
        return codTurma;
    }

    public void setCodTurma(Integer codTurma) {
        this.codTurma = codTurma;
    }

    public String getNomeTurma() {
        return nomeTurma;
    }

    public void setNomeTurma(String nomeTurma) {
        this.nomeTurma = nomeTurma;
    }


    public Integer getAnoLetivo() {
        return anoLetivo;
    }

    public void setAnoLetivo(Integer anoLetivo) {
        this.anoLetivo = anoLetivo;
    }
}