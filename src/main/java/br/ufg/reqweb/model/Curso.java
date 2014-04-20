/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

/**
 *
 * @author andre
 */
@Entity
public class Curso extends BaseModel {

    private static final long serialVersionUID = 1L;

    @Column(length = 100, nullable = false)
    @Size(min = 5, max = 100)
    private String nome;

    @Column(length = 6, unique = true, nullable = false)
    @Size(min = 2, max = 6)
    private String sigla;

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
        this.nome = nome.toUpperCase();
    }

    /**
     * @return the sigla
     */
    public String getSigla() {
        return sigla;
    }

    /**
     * @param sigla the sigla to set
     */
    public void setSigla(String sigla) {
        this.sigla = sigla.toUpperCase();
    }
}
