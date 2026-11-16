package br.ucsal.uniedu.model;

import jakarta.persistence.*;

@Entity
@Table(name = "disciplina")
public class Disciplina {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_disciplina")
    private Integer codDisciplina;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    public Integer getCodDisciplina() {
        return codDisciplina;
    }

    public void setCodDisciplina(Integer codDisciplina) {
        this.codDisciplina = codDisciplina;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
