/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.components;

import br.ufg.reqweb.dao.PeriodoDao;
import br.ufg.reqweb.model.Periodo;
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
public class PeriodoBean implements Serializable {
    
    public PeriodoBean() {
        periodo = new Periodo();
        itemSelecionado = null;        
        operation = null;
        termoBusca = "";
    }

    private static final long serialVersionUID = 1L;
    public static final String ADICIONA = "a";
    public static final String EDITA = "e";
    private String operation;
    private Periodo periodo;
    private Periodo itemSelecionado;
    private String termoBusca;        
    
    @Autowired
    private PeriodoDao periodoDao;

    private final ResourceBundle messages = ResourceBundle.getBundle(
            "locale.messages",
            FacesContext.getCurrentInstance().getViewRoot().getLocale());
    
    public void novoPeriodo(ActionEvent actionEvent) {
        setOperation(ADICIONA);
        periodo = new Periodo();
    }
    
    public void editaPeriodo(ActionEvent actionEvent) {
        if (getItemSelecionado() == null) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "info", messages.getString("itemSelecionar"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            setOperation(EDITA);
            periodo = getItemSelecionado();
        }        
    }
    
    public String excluiPeriodo() {
        FacesMessage msg;
        if (getItemSelecionado() == null) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "info", messages.getString("itemSelecionar"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        } else {
            periodoDao.excluir(itemSelecionado);
            itemSelecionado = null;
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "info", messages.getString("dadosExcluidos"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return listaPeriodos();
        }        
    }
    
    public String salvaPeriodo() {
        FacesMessage msg;
        RequestContext context = RequestContext.getCurrentInstance();
        if (periodo.getAno() < getMinAno() || periodo.getSemestre() == null || periodo.getDataInicio() == null || periodo.getDataTermino() == null) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "info", messages.getString("dadosInvalidos"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            context.addCallbackParam("resultado", false);
            return null;
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "info", messages.getString("dadosSalvos"));
            context.addCallbackParam("resultado", true);
            if (operation.equals(ADICIONA)) {
                periodoDao.adicionar(periodo);
                itemSelecionado = periodo;
            } else {
                periodoDao.atualizar(periodo);
            }
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        return listaPeriodos();
    }
    
    public List getSemestres() {
        List semestres = new ArrayList<>();
        semestres.addAll(Arrays.asList(Semestre.values()));
        return semestres;
    }
    
    public boolean isSelecionado() {
        return itemSelecionado != null;
    }

    public Periodo getItemSelecionado() {
        return itemSelecionado;
    }
    
    public void setItemSelecionado(Periodo itemSelecionado) {
        this.itemSelecionado = itemSelecionado;
    }

    public void selecionaItem(SelectEvent event) {
        itemSelecionado = (Periodo) event.getObject();
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
     * @return the periodo
     */
    public Periodo getPeriodo() {
        return periodo;
    }

    /**
     * @param periodo the periodo to set
     */
    public void setPeriodo(Periodo periodo) {
        this.periodo = periodo;
    }
    
    public List getFiltroPeriodos() {
        boolean found = termoBusca.matches("\\d{4}");
        if (!found) {
            return periodoDao.listar();
        }
        else {
            return periodoDao.procurar(termoBusca);
        }        
    }
    
    public List getPeriodos() {
        return periodoDao.listar();
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

    public String listaPeriodos() {
        return "periodos";
    }
    
    public int getMinAno() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    
}