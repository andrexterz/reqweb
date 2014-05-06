/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.model;

import br.ufg.reqweb.components.LocaleBean;

/**
 *
 * @author André
 */
public enum TipoItemRequerimentoEnum {
    
    DECLARACAO_DE_MATRICULA("declaracaoDeMatricula"),
    EXTRATO_ACADEMICO("extratoAcademico"),
    SEGUNDA_CHAMADA_DE_PROVA("segundaChamadaDeProva"),
    EMENTA_DE_DISCIPLINA("ementaDeDisciplina"),
    CONTRATO_DE_ESTAGIO("contratoDeEstagio"),
    RELATORIO_DE_ESTAGIO("relatorioDeEstagio"),
    INCLUSAO_DE_DISCIPLINA("inclusaoDeDisciplina"),
    EXCLUSAO_DE_DISCIPLINA("exclusaoDeDisciplina");

    private TipoItemRequerimentoEnum(String tipo) {
        this.tipo = tipo;
    }

    private final String tipo;

    public String getTipo() {
        return tipo;
    }
    
    public String getTipoLocale() {
        return LocaleBean.getMessageBundle().getString(tipo);
    }
}