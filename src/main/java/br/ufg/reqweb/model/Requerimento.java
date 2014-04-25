/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.model;

import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author andre
 */
@Entity
public class Requerimento extends BaseModel {
    
    private static final long serialVersionUID = 1L;
    
    @ManyToOne
    private Usuario discente;
    
    @Enumerated(EnumType.STRING)
    protected TipoRequerimentoEnum tipoRequerimento;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(insertable = true, updatable = false)
    private Date dataCriacao;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataModificacao;
    
    @Column
    private String observacao;
    
    @OneToMany(mappedBy = "requerimento", cascade = CascadeType.ALL)
    private List<ItemRequerimento> requerimentos;

    /**
     * @return the discente
     */
    public Usuario getDiscente() {
        return discente;
    }

    /**
     * @param discente the discente to set
     */
    public void setDiscente(Usuario discente) {
        this.discente = discente;
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
    
    /**
     * @return the dataCriacao
     */
    public Date getDataCriacao() {
        return dataCriacao;
    }

    /**
     * @param dataCriacao the dataCriacao to set
     */
    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    /**
     * @return the dataModificacao
     */
    public Date getDataModificacao() {
        return dataModificacao;
    }

    /**
     * @param dataModificacao the dataModificacao to set
     */
    public void setDataModificacao(Date dataModificacao) {
        this.dataModificacao = dataModificacao;
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
     * @return the requerimentos
     */
    public List<ItemRequerimento> getRequerimentos() {
        return requerimentos;
    }

    /**
     * @param requerimentos the requerimentos to set
     */
    public void setRequerimentos(List<ItemRequerimento> requerimentos) {
        this.requerimentos = requerimentos;
    }
    
    
}
