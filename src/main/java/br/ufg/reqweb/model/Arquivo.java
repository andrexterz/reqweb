/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 *
 * @author andre
 */

@Entity
public class Arquivo extends BaseModel {
    
    @NotNull
    private String mimetype;
    
    @NotNull
    private String nomeArquivo;
    
    @NotNull
    @ManyToOne
    private Usuario proprietario;

    /**
     * @return the mimetype
     */
    public String getMimetype() {
        return mimetype;
    }

    /**
     * @param mimetype the mimetype to set
     */
    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    /**
     * @return the nomeArquivo
     */
    public String getNomeArquivo() {
        return nomeArquivo;
    }

    /**
     * @param nomeArquivo the nomeArquivo to set
     */
    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    /**
     * @return the proprietario
     */
    public Usuario getProprietario() {
        return proprietario;
    }

    /**
     * @param proprietario the proprietario to set
     */
    public void setProprietario(Usuario proprietario) {
        this.proprietario = proprietario;
    }
    
}
