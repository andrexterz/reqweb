/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;


/**
 *
 * @author andre
 */
@Entity
public class Requerimento extends BaseModel {
    
    private static final long serialVersionUID = 1L;

    public Requerimento() {
        status = RequerimentoStatusEnum.ABERTO;
        itemRequerimentoList = new HashSet();
    }
    
    
    
    @ManyToOne
    @JoinColumn(name = "usuario_id", updatable = false)
    private Usuario discente;
    
    @OneToMany(mappedBy = "requerimento", orphanRemoval = true)
    private List<Atendimento> atendimentoList;
    
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "character varying(32)")
    private TipoRequerimentoEnum tipoRequerimento;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(insertable = true, updatable = false)
    private Date dataCriacao;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataModificacao;
    
    @Column
    private String observacao;
    
    @Cascade(CascadeType.ALL)
    @OneToMany(mappedBy = "requerimento", orphanRemoval = true )
    private Set<ItemRequerimento> itemRequerimentoList;
    
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "character(16) default 'ABERTO'")
    private RequerimentoStatusEnum status;    

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
     * @return the atendimentoList
     */
    public List<Atendimento> getAtendimentoList() {
        return atendimentoList;
    }

    /**
     * @param atendimentoList the atendimentoList to set
     */
    public void setAtendimentoList(List<Atendimento> atendimentoList) {
        this.atendimentoList = atendimentoList;
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
     * @return the itemRequerimentoList
     */
    public Set<ItemRequerimento> getItemRequerimentoList() {
        return itemRequerimentoList;
    }

    /**
     * @param itemRequerimentoList the itemRequerimentoList to set
     */
    public void setItemRequerimentoList(Set<ItemRequerimento> itemRequerimentoList) {
        this.itemRequerimentoList = itemRequerimentoList;
        for (ItemRequerimento ir: itemRequerimentoList) {
            ir.setRequerimento(this);
        }
    }
    
    public void addItemRequerimento(ItemRequerimento itemRequerimento) {
        itemRequerimentoList.add(itemRequerimento);
        itemRequerimento.setRequerimento(this);
    }
    
    public void removeItemRequerimento(ItemRequerimento itemRequerimento) {
    itemRequerimentoList.remove(itemRequerimento);
    }

    /**
     * @return the status
     */
    public RequerimentoStatusEnum getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(RequerimentoStatusEnum status) {
        this.status = status;
    }
}
