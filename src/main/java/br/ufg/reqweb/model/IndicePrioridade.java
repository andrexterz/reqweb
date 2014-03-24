/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.model;

import java.io.Serializable;
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
public class IndicePrioridade implements Serializable {
    
    @Id
    @SequenceGenerator(name = "INDICEPRIORIDADE_ID", sequenceName = "indiceprioridade_indiceprioridade_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "CURSO_ID", strategy = GenerationType.SEQUENCE)    
    private Long id;
    
    @ManyToOne
    private Usuario discente;
    
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
}
