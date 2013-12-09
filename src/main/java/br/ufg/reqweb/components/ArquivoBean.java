/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.components;

import br.ufg.reqweb.dao.PeriodoAjusteDao;
import br.ufg.reqweb.model.PeriodoAjuste;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.jsf.FacesContextUtils;

/**
 *
 * @author André
 */
@Component
public class ArquivoBean implements Serializable {

    public ArquivoBean() {
        this.periodoAjuste = new PeriodoAjuste();
    }
    
    private PeriodoAjuste periodoAjuste;

    public void uploadDisciplinas(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void uploadTurmas(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void uploadIndicePrioridade(FileUploadEvent event) {
        System.out.println("periodo Ajuste:" + periodoAjuste);
        UploadedFile file = event.getFile();
        FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
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

    public void selecionaPeriodoAjuste(ValueChangeEvent event) {
        ApplicationContext context = FacesContextUtils.getWebApplicationContext(FacesContext.getCurrentInstance());        
        PeriodoAjusteDao periodoAjusteDao = (PeriodoAjusteDao) context.getBean(PeriodoAjusteDao.class);
        periodoAjuste = periodoAjusteDao.buscar((Long) event.getNewValue());
        System.out.println("value changed...");
        FacesMessage msg = new FacesMessage("selected", periodoAjuste.toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

}
