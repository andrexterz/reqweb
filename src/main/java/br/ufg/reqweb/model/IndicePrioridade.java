/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 *
 * @author andre
 */

@Entity
public class IndicePrioridade extends BaseModel {
    
    @NotNull    
    @ManyToOne
    private Usuario discente;
    
    @NotNull
    @Column
    private float indicePrioridade;

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
}
