package br.ucsal.uniedu.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "material")
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_material")
    private Integer codMaterial;

    @Column(name = "titulo", nullable = false, length = 100)
    private String titulo;

    @Column(name = "tipo", nullable = false, length = 100)
    private String tipo;

    @Column(name = "url_conteudo", nullable = false)
    private String urlConteudo;

    @Column(name = "data_postagem", nullable = false)
    private LocalDate dataPostagem = LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "cod_disciplina", nullable = false)
    private Disciplina disciplina;

    @ManyToOne
    @JoinColumn(name = "cod_prof", nullable = false)
    private Professor professor;

    public Material() {
    }


    public Integer getCodMaterial() {
        return codMaterial;
    }

    public void setCodMaterial(Integer codMaterial) {
        this.codMaterial = codMaterial;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUrlConteudo() {
        return urlConteudo;
    }

    public void setUrlConteudo(String urlConteudo) {
        this.urlConteudo = urlConteudo;
    }

    public LocalDate getDataPostagem() {
        return dataPostagem;
    }

    public void setDataPostagem(LocalDate dataPostagem) {
        this.dataPostagem = dataPostagem;
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