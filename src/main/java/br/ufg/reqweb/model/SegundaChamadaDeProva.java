/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 *
 * @author andre
 */

@Entity
@DiscriminatorValue(value = "SC")
public class SegundaChamadaDeProva extends ItemRequerimento {

    public SegundaChamadaDeProva() {
        status = ItemRequerimentoStatusEnum.ABERTO;
        tipoItemRequerimento = TipoRequerimentoEnum.SEGUNDA_CHAMADA_DE_PROVA;
        arquivos = new ArrayList<>();
    }

    public SegundaChamadaDeProva(Turma turma, Date dataProvaA, Date dataProvaB, List<Arquivo> arquivos) {
        status = ItemRequerimentoStatusEnum.ABERTO;
        tipoItemRequerimento = TipoRequerimentoEnum.SEGUNDA_CHAMADA_DE_PROVA;
        this.turma = turma;
        this.dataProvaA = dataProvaA;
        this.dataProvaB = dataProvaB;
        this.arquivos = arquivos;
    }
    
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    private Turma turma;
    
    @Past
    @NotNull
    @Temporal(TemporalType.DATE)
    private Date dataProvaA;
    
    @Temporal(TemporalType.DATE)
    private Date dataProvaB;
    
    @Valid
    @OneToMany(mappedBy = "itemRequerimento",fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.SELECT)
    private List<Arquivo> arquivos;
    

    /**
     * @return the turma
     */
    public Turma getTurma() {
        return turma;
    }

    /**
     * @param turma the turma to set
     */
    public void setTurma(Turma turma) {
        this.turma = turma;
    }

    /**
     * @return the dataProvaA
     */
    public Date getDataProvaA() {
        return dataProvaA;
    }

    /**
     * @param dataProvaA the dataProvaA to set
     */
    public void setDataProvaA(Date dataProvaA) {
        this.dataProvaA = dataProvaA;
    }
    
    /**
     * @return the dataProvaB
     */
    public Date getDataProvaB() {
        return dataProvaB;
    }

    /**
     * @param dataProvaB the dataProvaB to set
     */
    public void setDataProvaB(Date dataProvaB) {
        this.dataProvaB = dataProvaB;
    }
    
    public List<Arquivo> getArquivos () {
        return arquivos;
    }

    public void setArquivos(List<Arquivo> arquivos) {
        this.arquivos = arquivos;
    }
    
    public void addArquivo(Arquivo arquivo) {
        arquivos.add(arquivo);
        arquivo.setItemRequerimento(this);
    }
    
    public void removeArquivo(Arquivo arquivo) {
        arquivos.remove(arquivo);
    }
    
    
    
}
