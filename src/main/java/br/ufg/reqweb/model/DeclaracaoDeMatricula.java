/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 *
 * @author andre
 */

@Entity
@Table(name = "declaracaodematricula")
@DiscriminatorValue(value = "DM")
public class DeclaracaoDeMatricula extends ItemRequerimento {

    public DeclaracaoDeMatricula() {
        //min copias
        numeroCopias = 1;
        //status
        status = ItemRequerimentoStatusEnum.ABERTO;
                
    }
    
    @Min(value = 1)
    @Max(value = 2)
    @Column(columnDefinition = "integer default 1")
    private int numeroCopias;

    public int getNumeroCopias() {
        return numeroCopias;
    }

    public void setNumeroCopias(int numeroCopias) {
        this.numeroCopias = numeroCopias;
    }
}
