/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.model;

import br.ufg.reqweb.components.LocaleBean;

/**
 *
 * @author andre
 */
public enum TipoRequerimentoEnum {
    
    DECLARACAO_DE_MATRICULA("declaracaoDeMatricula"),
    EXTRATO_ACADEMICO("extratoAcademico"),
    SEGUNDA_CHAMADA_DE_PROVA("segundaChamadaDeProva"),    
    EMENTA_DE_DISCIPLINA("ementaDeDisciplina"),
    DOCUMENTO_DE_ESTAGIO("documentoDeEstagio"),
    AJUSTE_DE_MATRICULA("ajusteDeMatricula");
    
    private TipoRequerimentoEnum(String tipo) {
        this.tipo = tipo;
    }
    
    private final String tipo;
    
    public String getTipo() {
        return this.tipo;
    }
    
    public String getTipoLocale() {
        return LocaleBean.getMessageBundle().getString(tipo);
    }    
    
}
