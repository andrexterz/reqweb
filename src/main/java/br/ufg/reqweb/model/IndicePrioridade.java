/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 *
 * @author andre
 */

@Entity
public class IndicePrioridade implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull    
    @ManyToOne
    private Usuario discente;
    
    @NotNull
    @Column
    private float indicePrioridade;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

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
        if (discente != null) {
            PerfilEnum perfilValido = PerfilEnum.DISCENTE;
            boolean valido = false;
            for (Perfil p : discente.getPerfilList()) {
                if (p.getTipoPerfil().getPapel().equals(perfilValido.getPapel())) {
                    valido = true;
                    perfilValido = p.getTipoPerfil();
                    break;
                }
            }
            if (valido) {
                this.discente = discente;
            } else {
                throw new NullPointerException(String.format("Invalid User Group:%s ", perfilValido.getGrupo()));
            }
        } else {
            this.discente = null;
        }
    }

    /**
     * @return the indicePrioridade
     */
    public float getIndicePrioridade() {
        return indicePrioridade;
    }

    /**
     * @param indicePrioridade the indicePrioridade to set
     */
    public void setIndicePrioridade(float indicePrioridade) {
        this.indicePrioridade = indicePrioridade;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj != null) {
            return ((obj instanceof IndicePrioridade) && ((long) ((id == null) ? Long.MIN_VALUE : id)) == (long) ((IndicePrioridade) obj).getId());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (id != null) ? 11 * 7 + (int) (id ^ (id >>> 32)) : super.hashCode();
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "@" + id;
    }    
}
