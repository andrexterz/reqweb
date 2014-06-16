/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.model;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

/**
 *
 * @author andre
 */
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"tipoPerfil","usuario_id", "curso_id"})})
public class Perfil extends BaseModel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    
    public static PerfilEnum[] perfilCursoMustBeNull = {
            PerfilEnum.ADMINISTRADOR,
            PerfilEnum.DOCENTE,
            PerfilEnum.SECRETARIA
        };

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "character varying(32)")
    private PerfilEnum tipoPerfil;

    @NotNull
    @ManyToOne
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    private Curso curso;

    /**
     * @return the perfilTipo
     */
    public PerfilEnum getTipoPerfil() {
        return tipoPerfil;
    }

    /**
     * @param tipoPerfil the perfilTipo to set
     */
    public void setTipoPerfil(PerfilEnum tipoPerfil) {
        this.tipoPerfil = tipoPerfil;
    }

    /**
     * @return the usuario
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * @param usuario the usuario to set
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * @return the curso
     */
    public Curso getCurso() {
        return curso;
    }

    /**
     * @param curso the curso to set
     */
    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass() == this.getClass()) {
            Perfil other = (Perfil) obj;
            return (Objects.equals(other.getTipoPerfil(), this.getTipoPerfil())
                    && Objects.equals(other.getUsuario(), this.getUsuario())
                    && Objects.equals(other.getCurso(), this.getCurso()));
        }
        return false;
    }
    
    

}
