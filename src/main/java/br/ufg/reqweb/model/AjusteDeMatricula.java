/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.model;

import br.ufg.reqweb.components.LocaleBean;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

/**
 *
 * @author andre
 */

@Entity
@DiscriminatorValue(value = "AM")
public class AjusteDeMatricula extends ItemRequerimento {

    public AjusteDeMatricula() {
        status = ItemRequerimentoStatusEnum.ABERTO;
        tipoItemRequerimento = TipoItemRequerimentoEnum.AJUSTE_DE_MATRICULA;
    }

    public enum TipoDeAjuste {
        INCLUSAO_DE_DISCIPLINA("inclusaoDeDisciplina"),
        EXCLUSAO_DE_DISCIPLINA("exclusaoDeDisciplina");

        private TipoDeAjuste(String tipo) {
            this.tipo = null;
        }
        
        private final String tipo;

        public String getTipo() {
            return tipo;
        }

        public String getTipoLocale() {
            return LocaleBean.getMessageBundle().getString(tipo);
        }        
    }
    
    @Enumerated(EnumType.STRING)
    private TipoDeAjuste tipoDeAjuste;
    
    @ManyToOne
    private Turma turma;
    
    /**
     * @return the tipoDeAjuste
     */
    public TipoDeAjuste getTipoDeAjuste() {
        return tipoDeAjuste;
    }

    /**
     * @param tipoDeAjuste the tipoDeAjuste to set
     */
    public void setTipoDeAjuste(TipoDeAjuste tipoDeAjuste) {
        this.tipoDeAjuste = tipoDeAjuste;
    }

    /**
     * @return the turma
     */
    public Turma getTurma() {
        return turma;
    }

    /**
     * @param turma the turma to set
     */
    public void setTurma(Turma turma) {
        this.turma = turma;
    }    
}
