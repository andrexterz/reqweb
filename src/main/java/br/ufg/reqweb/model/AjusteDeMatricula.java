/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.model;

import br.ufg.reqweb.components.LocaleBean;
import java.util.Objects;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 *
 * @author andre
 */

@Entity
@DiscriminatorValue(value = "AM")
public class AjusteDeMatricula extends ItemRequerimento {

    public AjusteDeMatricula() {
        status = ItemRequerimentoStatusEnum.ABERTO;
        tipoItemRequerimento = TipoRequerimentoEnum.AJUSTE_DE_MATRICULA;
    }

    public AjusteDeMatricula(TipoDeAjuste tipoDeAjuste, Turma turma) {
        status = ItemRequerimentoStatusEnum.ABERTO;
        tipoItemRequerimento = TipoRequerimentoEnum.AJUSTE_DE_MATRICULA;        
        this.tipoDeAjuste = tipoDeAjuste;
        this.turma = turma;
    }

    public enum TipoDeAjuste {
        INCLUSAO_DE_DISCIPLINA("inclusaoDeDisciplina"),
        EXCLUSAO_DE_DISCIPLINA("exclusaoDeDisciplina");

        private TipoDeAjuste(String tipo) {
            this.tipo = tipo;
        }
        
        private final String tipo;

        public String getTipo() {
            return tipo;
        }

        public String getTipoLocale() {
            return LocaleBean.getDefaultMessageBundle().getString(tipo);
        }        
    }
    
    @NotNull
    @Enumerated(EnumType.STRING)
    private TipoDeAjuste tipoDeAjuste;
    
    @NotNull
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
    
    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass() == this.getClass()) {
            AjusteDeMatricula other = (AjusteDeMatricula) obj;
            return Objects.equals(other.getTurma(), this.getTurma());
        }
        return false;
    }
}
