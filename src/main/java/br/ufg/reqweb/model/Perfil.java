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
import javax.persistence.SequenceGenerator;

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
    @SequenceGenerator(name = "PERFIL_ID", sequenceName = "perfil_perfil_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "PERFIL_ID", strategy = GenerationType.SEQUENCE)
    private Long id;
    
    
    @Enumerated(EnumType.STRING)
    private PerfilEnum tipoPerfil;

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
    
    @Override
    public boolean equals(Object obj) {
        if (obj != null) {
            return ((obj instanceof Perfil) && ((long) ((getId() == null) ? Long.MIN_VALUE: getId())) == (long) ((Perfil) obj).getId());
        } else {
            return false;
        }
        
    }

    @Override
    public int hashCode() {
        return (getId() != null) ? 17 * 11 + (int) (getId() ^ (getId() >>> 32)): super.hashCode();
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "@" + getId();
    }    
}
