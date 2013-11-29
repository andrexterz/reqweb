/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

/**
 *
 * @author andre
 */

@Entity
public class Unidade implements Serializable {
    
    @Id
    @SequenceGenerator(name = "UNIDADE_ID", sequenceName = "unidade_unidade_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "UNIDADE_ID", strategy = GenerationType.SEQUENCE)
    Long id;
    
    @Column
    private String nome;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
