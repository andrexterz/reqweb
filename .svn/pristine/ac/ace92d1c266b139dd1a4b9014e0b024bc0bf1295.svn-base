/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.model;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "PERFIL_ID", sequenceName = "perfil_perfil_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "PERFIL_ID", strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @Column
    private String grupo;
    
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Curso curso;
    
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Usuario usuario;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    
}
