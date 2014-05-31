/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.model;

import br.ufg.reqweb.components.LocaleBean;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

/**
 *
 * @author andre
 */
@Entity
@DiscriminatorValue(value = "DE")
public class DocumentoDeEstagio extends ItemRequerimento {
    
    public DocumentoDeEstagio() {
        status = ItemRequerimentoStatusEnum.ABERTO;
        tipoItemRequerimento = TipoRequerimentoEnum.DOCUMENTO_DE_ESTAGIO;
    }

    public DocumentoDeEstagio(TipoDeDocumento tipoDeDocumento) {
        status = ItemRequerimentoStatusEnum.ABERTO;
        tipoItemRequerimento = TipoRequerimentoEnum.DOCUMENTO_DE_ESTAGIO;        
        this.tipoDeDocumento = tipoDeDocumento;
    }

    public enum TipoDeDocumento {

        CONTRATO_DE_ESTAGIO("contratoDeEstagio"),
        RELATORIO_DE_ESTAGIO("relatorioDeEstagio");

        private TipoDeDocumento(String tipo) {
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
    
    @NotNull
    @Enumerated(EnumType.STRING)
    private TipoDeDocumento tipoDeDocumento;
    
    @Column(columnDefinition = "boolean default false")
    private boolean recebido;


    public TipoDeDocumento getTipoDeDocumento() {
        return tipoDeDocumento;
    }

    public void setTipoDeDocumento(TipoDeDocumento tipoDeDocumento) {
        this.tipoDeDocumento = tipoDeDocumento;
    }

    public boolean isRecebido() {
        return recebido;
    }

    public void setRecebido(boolean recebido) {
        this.recebido = recebido;
    }
    
}
