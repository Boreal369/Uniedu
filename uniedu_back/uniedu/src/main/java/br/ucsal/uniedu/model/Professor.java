package br.ucsal.uniedu.model;

import jakarta.persistence.*;

@Entity
@Table(name = "professor")
public class Professor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_prof")
    private Integer codProf;

    @OneToOne
    @JoinColumn(name = "cod_usuario", nullable = false, unique = true)
    private Usuario usuario;

    public Professor() {
    }

    public Integer getCodProf() {
        return codProf;
    }

    public void setCodProf(Integer codProf) {
        this.codProf = codProf;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}