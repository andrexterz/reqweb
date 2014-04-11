/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 *
 * @author andre
 */
@Entity
public class Perfil implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PerfilEnum tipoPerfil;

    @NotNull
    @ManyToOne
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    private Curso curso;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

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

    /**
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if ((obj != null) && (obj.getClass() == this.getClass())) {
            Perfil other = (Perfil) obj;
            if (other.getId() != null && this.getId() != null) {
                return other.getId().longValue() == this.getId().longValue();
            } else {
                return other.getId() == this.getId();
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (getId() != null) ? 17 * 11 + (int) (getId() ^ (getId() >>> 32)) : super.hashCode();
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "@" + getId();
    }
}
