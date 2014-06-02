/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 *
 * @author andre
 */

@Entity
public class Atendimento extends BaseModel {
     
    private static final long serialVersionUID = 1L; 

    @OneToOne(mappedBy = "atendimento")
    private Requerimento requerimento;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id")
    private Usuario atendente;
    
    @Column
    private String observacao;
    
    /**
     * @return the requerimento
     */
    public Requerimento getRequerimento() {
        return requerimento;
    }
    
    /**
     * 
     * @param requerimento 
     */
    public void setRequerimento(Requerimento requerimento) {
        this.requerimento = requerimento;
    }
    
    /**
     * @return the atendente
     */
    public Usuario getAtendente() {
        return atendente;
    }

    /**
     * @param atendente the atendente to set
     */
    public void setAtendente(Usuario atendente) {
        this.atendente = atendente;
    }

    /**
     * @return the observacao
     */
    public String getObservacao() {
        return observacao;
    }

    /**
     * @param observacao the observacao to set
     */
    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
