/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.components;

import br.ufg.reqweb.dao.PeriodoAjusteDao;
import br.ufg.reqweb.model.PeriodoAjuste;
import br.ufg.reqweb.model.Semestre;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author André
 */

@Component
public class PeriodoAjusteBean implements Serializable {
    
    public PeriodoAjusteBean() {
        periodoAjuste = new PeriodoAjuste();
        itemSelecionado = null;        
        operation = null;
        termoBusca = "";
    }

    private static final long serialVersionUID = 1L;
    public static final String ADICIONA = "a";
    public static final String EDITA = "e";
    private String operation;
    private PeriodoAjuste periodoAjuste;
    private PeriodoAjuste itemSelecionado;
    private String termoBusca;        
    
    @Autowired
    private PeriodoAjusteDao periodoAjusteDao;

    private final ResourceBundle messages = ResourceBundle.getBundle(
            "locale.messages",
            FacesContext.getCurrentInstance().getViewRoot().getLocale());
    
    public void novoAjuste(ActionEvent actionEvent) {
        setOperation(ADICIONA);
        periodoAjuste = new PeriodoAjuste();
    }
    
    public void editaAjuste(ActionEvent actionEvent) {
        if (getItemSelecionado() == null) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "info", messages.getString("itemSelecionar"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            setOperation(EDITA);
            periodoAjuste = getItemSelecionado();
        }        
    }
    
    public String excluiPeriodoAjuste() {
        FacesMessage msg;
        if (getItemSelecionado() == null) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "info", messages.getString("itemSelecionar"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        } else {
            periodoAjusteDao.excluir(itemSelecionado);
            itemSelecionado = null;
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "info", messages.getString("dadosExcluidos"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return listaPeriodoAjustes();
        }        
    }
    
    public String salvaPeriodoAjuste() {
        FacesMessage msg;
        RequestContext context = RequestContext.getCurrentInstance();
        if (periodoAjuste.getAno() < getMinAno() || periodoAjuste.getSemestre() == null || periodoAjuste.getDataInicio() == null || periodoAjuste.getDataTermino() == null) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "info", messages.getString("dadosInvalidos"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            context.addCallbackParam("resultado", false);
            return null;
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "info", messages.getString("dadosSalvos"));
            context.addCallbackParam("resultado", true);
            if (operation.equals(ADICIONA)) {
                periodoAjusteDao.adicionar(periodoAjuste);
                itemSelecionado = periodoAjuste;
            } else {
                periodoAjusteDao.atualizar(periodoAjuste);
            }
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        return listaPeriodoAjustes();
    }
    
    public List getSemestres() {
        List semestres = new ArrayList<>();
        semestres.addAll(Arrays.asList(Semestre.values()));
        return semestres;
    }
    
    public boolean isSelecionado() {
        return itemSelecionado != null;
    }

    public PeriodoAjuste getItemSelecionado() {
        return itemSelecionado;
    }
    
    public void setItemSelecionado(PeriodoAjuste itemSelecionado) {
        this.itemSelecionado = itemSelecionado;
    }

    public void selecionaItem(SelectEvent event) {
        itemSelecionado = (PeriodoAjuste) event.getObject();
    }
        
    
    /**
     * @return the operation
     */
    public String getOperation() {
        return operation;
    }

    /**
     * @param operation the operation to set
     */
    public void setOperation(String operation) {
        this.operation = operation;
    }

    /**
     * @return the periodoAjuste
     */
    public PeriodoAjuste getPeriodoAjuste() {
        return periodoAjuste;
    }

    /**
     * @param periodoAjuste the periodoAjuste to set
     */
    public void setPeriodoAjuste(PeriodoAjuste periodoAjuste) {
        this.periodoAjuste = periodoAjuste;
    }
    
    public List getFiltroPeriodoAjustes() {
        boolean found = termoBusca.matches("\\d{4}");
        if (!found) {
            return periodoAjusteDao.listar();
        }
        else {
            return periodoAjusteDao.procurar(termoBusca);
        }        
    }
    
    public List getPeriodoAjustes() {
        return periodoAjusteDao.listar();
    }

    /**
     * @return the termoBusca
     */
    public String getTermoBusca() {
        return termoBusca;
    }

    /**
     * @param termoBusca the termoBusca to set
     */
    public void setTermoBusca(String termoBusca) {
        this.termoBusca = termoBusca;
    }

    public String listaPeriodoAjustes() {
        return "periodoAjustes";
    }
    
    public int getMinAno() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    
}
