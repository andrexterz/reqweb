/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.model;

import javax.persistence.Entity;

/**
 *
 * @author andre
 */

@Entity
public class DeclaracaoDeMatricula extends ItemRequerimento {

    public DeclaracaoDeMatricula() {
        this.tipoRequerimento = TipoRequerimentoEnum.DECLARACAO_DE_MATRICULA;
    }
}
