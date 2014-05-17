/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 *
 * @author andre
 */

@Entity
@DiscriminatorValue(value = "ED")
public class EmentaDeDisciplina extends ItemRequerimento {

    public EmentaDeDisciplina() {
        status = ItemRequerimentoStatusEnum.ABERTO;
        tipoItemRequerimento = TipoItemRequerimentoEnum.EMENTA_DE_DISCIPLINA;
    }

    public EmentaDeDisciplina(Disciplina disciplina) {
        status = ItemRequerimentoStatusEnum.ABERTO;
        tipoItemRequerimento = TipoItemRequerimentoEnum.EMENTA_DE_DISCIPLINA;
        this.disciplina = disciplina;
    }
    
    @NotNull
    @ManyToOne
    private Disciplina disciplina;

    /**
     * @return the disciplina
     */
    public Disciplina getDisciplina() {
        return disciplina;
    }

    /**
     * @param disciplina the disciplina to set
     */
    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }

}
