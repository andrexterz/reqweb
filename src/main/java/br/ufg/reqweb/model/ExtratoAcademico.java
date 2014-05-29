/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 * @author andre
 */

@Entity
@DiscriminatorValue(value = "EA")
public class ExtratoAcademico extends ItemRequerimento {

    public ExtratoAcademico() {
        status = ItemRequerimentoStatusEnum.ABERTO;
        tipoItemRequerimento = TipoRequerimentoEnum.EXTRATO_ACADEMICO;
    }
    

}
