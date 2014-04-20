/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.model;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.validation.constraints.NotNull;

/**
 *
 * @author andre
 */
@Entity
public class Permissao extends BaseModel {
    
    
    @Column(unique = true)
    private String nome;
    
    @NotNull
    private String url;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<PerfilEnum> tipoPerfil;


    /**
     * @return the nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * @param nome the nome to set
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the tipoPerfil
     */
    public List<PerfilEnum> getTipoPerfil() {
        return tipoPerfil;
    }

    /**
     * @param tipoPerfil the tipoPerfil to set
     */
    public void setTipoPerfil(List<PerfilEnum> tipoPerfil) {
        this.tipoPerfil = tipoPerfil;
    }
    
    public void adicionaPerfil(PerfilEnum perfil) {
        this.tipoPerfil.add(perfil);
    }
    
    public void removePerfil(PerfilEnum perfil) {
        this.tipoPerfil.remove(perfil);
    }
    
}
