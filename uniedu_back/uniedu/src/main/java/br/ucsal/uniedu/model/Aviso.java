package br.ucsal.uniedu.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "aviso")
public class Aviso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_aviso")
    private Integer codAviso;

    @Column(name = "titulo", nullable = false, length =100)
    private String titulo;

    @Column(name = "mensagem", nullable = false)
    private String mensagem;

    @Column(name = "data_publi", nullable = false)
    private LocalDate dataPublico = LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "cod_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "destino", nullable = false, length = 20)
    private String destino = "TODOS";

    @ManyToOne
    @JoinColumn(name = "cod_turma")
    private Turma turma;

    public Aviso() {
    }

    public Integer getCodAviso() {
        return codAviso;
    }

    public void setCodAviso(Integer codAviso) {
        this.codAviso = codAviso;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public LocalDate getDataPublico() {
        return dataPublico;
    }

    public void setDataPublico(LocalDate dataPublico) {
        this.dataPublico = dataPublico;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public Turma getTurma() {
        return turma;
    }

    public void setTurma(Turma turma) {
        this.turma = turma;
    }
}