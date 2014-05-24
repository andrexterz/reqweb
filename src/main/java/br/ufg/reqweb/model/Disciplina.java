/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
 *
 * @author André
 */

@Entity
public class Disciplina extends BaseModel {
    
    private static final long serialVersionUID = 1L;
    
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
    public String toString() {
        return String.format("%s: %s", nome, curso.getSigla());
    }    
}
