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
import java.util.List;
import java.util.Set;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author André
 */
@Component
@Scope(value = "session")
public class PeriodoBean implements Serializable {

    public PeriodoBean() {
        periodo = new Periodo();
        itemSelecionado = null;
        operation = null;
        termoBusca = "";
        saveStatus = false;
    }

    @Autowired
    Validator validator;

    @Autowired
    private PeriodoDao periodoDao;

    private static final long serialVersionUID = 1L;
    private static final String ADICIONA = "a";
    private static final String EDITA = "e";
    private boolean saveStatus;
    private String operation;
    private Periodo periodo;
    private Periodo itemSelecionado;
    private String termoBusca;

    public void novoPeriodo() {
        setOperation(ADICIONA);
        periodo = new Periodo();
    }

    public void editaPeriodo() {
        if (!isSelecionado()) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "info", LocaleBean.getMessageBundle().getString("itemSelecionar"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            setOperation(EDITA);
            periodo = getItemSelecionado();
        }
    }

    public void excluiPeriodo() {
        FacesMessage msg;
        if (!isSelecionado()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "info", LocaleBean.getMessageBundle().getString("itemSelecionar"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            periodoDao.excluir(itemSelecionado);
            itemSelecionado = null;
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "info", LocaleBean.getMessageBundle().getString("dadosExcluidos"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void salvaPeriodo() {
        RequestContext context = RequestContext.getCurrentInstance();
        try {
            Set<ConstraintViolation<Periodo>> errors = validator.validate(periodo);
            saveStatus = errors.isEmpty();
            if (saveStatus) {
                if (operation.equals(ADICIONA)) {
                    periodoDao.adicionar(periodo);
                    itemSelecionado = periodo;
                } else {
                    periodoDao.atualizar(periodo);
                }
            }

        } catch (Exception e) {
            setSaveStatus(false);
        }
        context.addCallbackParam("resultado", saveStatus);
        handleCompleteSavePeriodo();
    }
    
    public void handleCompleteSavePeriodo() {
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage msg;
        if (saveStatus) {
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, LocaleBean.getMessageBundle().getString("dadosSalvos"), "");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "info", LocaleBean.getMessageBundle().getString("dadosInvalidos"));
        }
        context.addMessage(null, msg);
    }    

    public List<Periodo> findPeriodo(String query) {
        return periodoDao.find(query);
    }

    public List<Semestre> getSemestres() {
        List<Semestre> semestres = new ArrayList<>();
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

    public List<Periodo> getFiltroPeriodos() {
        boolean found = termoBusca.matches("\\d{4}");
        if (!found) {
            return periodoDao.findAll();
        } else {
            return periodoDao.find(termoBusca);
        }
    }

    public List<Periodo> getPeriodos() {
        return periodoDao.findAll();
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

    public boolean isSaveStatus() {
        return saveStatus;
    }

    public void setSaveStatus(boolean saveStatus) {
        this.saveStatus = saveStatus;
    }
    
}
