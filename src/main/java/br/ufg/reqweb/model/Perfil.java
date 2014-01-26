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
    private PerfilEnum perfil;

    @ManyToOne
    private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.EAGER)
    private Curso curso;

    /**
     * @return the perfil
     */
    public PerfilEnum getPerfil() {
        return perfil;
    }

    /**
     * @param perfil the perfil to set
     */
    public void setPerfil(PerfilEnum perfil) {
        this.perfil = perfil;
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
}
