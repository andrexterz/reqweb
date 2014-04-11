/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 *
 * @author andre
 */
@Entity
public class Permissao implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String nome;
    
    @NotNull
    private String url;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<PerfilEnum> tipoPerfil;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
    
    @Override
    public boolean equals(Object obj) {
        if ((obj != null) && (obj.getClass() == this.getClass())) {
            Permissao other = (Permissao) obj;
            if (other.getId() != null && this.getId() != null) {
                return other.getId().longValue() == this.getId().longValue();
            } else {
                return other.getId() == this.getId();
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (getId() != null) ? 17 * 11 + (int) (getId() ^ (getId() >>> 32)): super.hashCode();
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "@" + getId();
    }    
    
}
