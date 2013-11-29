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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;


/**
 *
 * @author andre
 */
@Entity
public class Curso implements Serializable {


    @Id
    @SequenceGenerator(name = "CURSO_ID", sequenceName = "curso_curso_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "CURSO_ID", strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @Column
    private String nome;
    
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "UNIDADE_ID")
    private Unidade unidade;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the unidade
     */
    public Unidade getUnidade() {
        return unidade;
    }

    /**
     * @param unidade the unidade to set
     */
    public void setUnidade(Unidade unidade) {
        this.unidade = unidade;
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
        this.nome = nome;
    }
}
