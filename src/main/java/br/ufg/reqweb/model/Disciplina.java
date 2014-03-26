/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
 *
 * @author André
 */

@Entity
public class Disciplina implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(unique = true)
    private Long codigo;
    
    @Size(min = 2)
    @Column
    private String nome;
    
    @ManyToOne
    @JoinColumn(name = "curso_id")
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
     * @return the codigo
     */
    public Long getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    /**
     * @return the nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * @param nome the nome to set
     */
    public void setNome(String nome) {
        this.nome = nome.toUpperCase();
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
            return ((obj instanceof Disciplina) && ((long) ((id == null) ? Long.MIN_VALUE: id)) == (long) ((Disciplina) obj).getId());
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
