package br.ucsal.uniedu.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "documento")
public class Documento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_documento")
    private Integer codDocumento;

    @Column(name = "titulo", nullable = false, length = 100)
    private String titulo;

    @Column(name = "tipo", nullable = false, length = 50)
    private String tipo;

    @Column(name = "url_arquivo", nullable = false)
    private String urlArquivo;

    @Column(name = "data_envio", nullable = false)
    private LocalDate dataEnvio = LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "cod_aluno", nullable = false)
    private Aluno aluno;

    public Documento() {
    }


    public Integer getCodDocumento() {
        return codDocumento;
    }

    public void setCodDocumento(Integer codDocumento) {
        this.codDocumento = codDocumento;
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

    public String getUrlArquivo() {
        return urlArquivo;
    }

    public void setUrlArquivo(String urlArquivo) {
        this.urlArquivo = urlArquivo;
    }

    public LocalDate getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(LocalDate dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }
}