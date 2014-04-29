/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;

/**
 *
 * @author andre
 */

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public abstract class ItemRequerimento extends BaseModel {
    
    private static final long serialVersionUID = 1L;
    
    @ManyToOne
    protected Requerimento requerimento;
    
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "character varying(16) default 'ABERTO'")
    protected ItemRequerimentoStatusEnum status;
    
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
     * @return the status
     */
    public ItemRequerimentoStatusEnum getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(ItemRequerimentoStatusEnum status) {
        this.status = status;
    }
}
