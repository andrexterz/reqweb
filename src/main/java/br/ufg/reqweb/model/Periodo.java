/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.model;

import br.ufg.reqweb.model.validators.ValidYear;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.ValidationException;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

/**
 *
 * @author André
 */
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"ano","semestre"})})
public class Periodo extends BaseModel {

    private static final long serialVersionUID = 1L;

    public Periodo() {
        ativo = false;
    }

    public Periodo(int ano, Semestre semestre, Date dataInicio, Date dataTermino, boolean ativo) {
        this.ano = ano;
        this.semestre = semestre;
        this.dataInicio = dataInicio;
        this.dataTermino = dataTermino;
        this.ativo = ativo;
    }

    @ValidYear
    @Column
    private int ano;

    @NotNull
    @Enumerated(EnumType.ORDINAL)
    private Semestre semestre;
    
    @NotNull
    @Temporal(TemporalType.DATE)
    private Date dataInicio;

    @NotNull
    @Temporal(TemporalType.DATE)
    private Date dataTermino;
    
    @Column(columnDefinition = "boolean default false")
    private boolean ativo;

  
    /**
     * @return the ano
     */
    public int getAno() {
        return ano;
    }

    /**
     * @param ano the ano to set
     */
    public void setAno(int ano) {
        this.ano = ano;
    }

    /**
     * @return the semestre
     */
    public Semestre getSemestre() {
        return semestre;
    }

    /**
     * @param semestre the semestre to set
     */
    public void setSemestre(Semestre semestre) {
        this.semestre = semestre;
    }

    /**
     * @return the dataInicio
     */
    public Date getDataInicio() {
        return dataInicio;
    }

    /**
     * @param dataInicio the dataInicio to set
     */
    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    /**
     * @return the dataTermino
     */
    public Date getDataTermino() {
        return dataTermino;
    }
    
    /**
     * @param dataTermino the dataTermino to set
     */
    public void setDataTermino(Date dataTermino) {
        this.dataTermino = dataTermino;
    }
    
    @AssertTrue
    public boolean isValidPeriodo() {
        Calendar yearDataInicio = Calendar.getInstance();
        Calendar yearDataTermino = Calendar.getInstance();
        yearDataInicio.setTime(dataInicio);
        yearDataTermino.setTime(dataInicio);
        return (ano == yearDataInicio.get(Calendar.YEAR) && ano == yearDataTermino.get(Calendar.YEAR) && dataTermino.getTime() >= dataInicio.getTime());
    }

    /**
     * 
     * @return the ativo
     */

    public boolean isAtivo() {
        return ativo;
    }
    
    /**
     * 
     * @param ativo 
     */

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
    
    @Override
    public String toString() {
        return String.format("%d/%d",ano,semestre.getValue());
    }
}
