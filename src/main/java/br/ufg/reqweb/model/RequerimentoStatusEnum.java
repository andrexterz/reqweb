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
public enum RequerimentoStatusEnum {
    
    ABERTO("aberto"),
    EM_ANDAMENTO("emAndamento"),
    FINALIZADO("finalizado");

    private RequerimentoStatusEnum(String status) {
        this.status = status;
    }
    
    private final String status;

    public String getStatus() {
        return status;
    }
    
    public String getStatusLocale() {
        return LocaleBean.getDefaultMessageBundle().getString(status);
    }
}
