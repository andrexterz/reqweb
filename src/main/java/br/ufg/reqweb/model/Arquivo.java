/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 *
 * @author andre
 */

@Entity
public class Arquivo extends BaseModel {

    public Arquivo() {
    }

    public Arquivo(String nomeArquivo, byte[] conteudo, String mimetype, SegundaChamadaDeProva itemRequerimento) {
        this.nomeArquivo = nomeArquivo;
        this.conteudo = conteudo;
        this.mimetype = mimetype;
        this.itemRequerimento = itemRequerimento;
    }
    
    @NotNull
    @Column
    private String nomeArquivo;
    
    @NotNull
    @Lob
    private byte[] conteudo;

    @NotNull
    @Column
    private String mimetype;
    
    @NotNull
    @ManyToOne
    private SegundaChamadaDeProva itemRequerimento;

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
     * @return the conteudo
     */
    public byte[] getConteudo() {
        return conteudo;
    }

    /**
     * @param conteudo the conteudo to set
     */
    public void setConteudo(byte[] conteudo) {
        this.conteudo = conteudo;
    }

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
     * @return the itemRequerimento
     */
    public SegundaChamadaDeProva getItemRequerimento() {
        return itemRequerimento;
    }

    /**
     * @param itemRequerimento the itemRequerimento to set
     */
    public void setItemRequerimento(SegundaChamadaDeProva itemRequerimento) {
        this.itemRequerimento = itemRequerimento;
    }
}
