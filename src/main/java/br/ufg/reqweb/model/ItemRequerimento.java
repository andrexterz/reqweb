/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

/**
 *
 * @author andre
 */

@Entity
public abstract class ItemRequerimento extends BaseModel {
    
    @ManyToOne
    protected Requerimento requerimento;
    
    @Enumerated(EnumType.STRING)
    protected TipoRequerimentoEnum tipoRequerimento;

    /**
     * @return the requerimento
     */
    public Requerimento getRequerimento() {
        return requerimento;
    }

    /**
     * @param requerimento the requerimento to set
     */
    public void setRequerimento(Requerimento requerimento) {
        this.requerimento = requerimento;
    }

    /**
     * @return the tipoRequerimento
     */
    public TipoRequerimentoEnum getTipoRequerimento() {
        return tipoRequerimento;
    }

    /**
     * @param tipoRequerimento the tipoRequerimento to set
     */
    public void setTipoRequerimento(TipoRequerimentoEnum tipoRequerimento) {
        this.tipoRequerimento = tipoRequerimento;
    }
    
}
