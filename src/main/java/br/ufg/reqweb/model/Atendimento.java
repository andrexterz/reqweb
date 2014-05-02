/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author andre
 */

@Entity
public class Atendimento extends BaseModel {
     
    private static final long serialVersionUID = 1L; 

    @ManyToOne
    private Requerimento requerimento;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", updatable = false)
    private Usuario atendente;
    
    @Column
    private String observacao;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAtendimento;

    /**
     * @return the requerimento
     */
    public Requerimento getRequerimento() {
        return requerimento;
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

    /**
     * @return the dataAtendimento
     */
    public Date getDataAtendimento() {
        return dataAtendimento;
    }

    /**
     * @param dataAtendimento the dataAtendimento to set
     */
    public void setDataAtendimento(Date dataAtendimento) {
        this.dataAtendimento = dataAtendimento;
    }
}
