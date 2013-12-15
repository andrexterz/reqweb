/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.components;

import br.ufg.reqweb.dao.PeriodoDao;
import br.ufg.reqweb.model.Periodo;
import br.ufg.reqweb.util.CSVParser;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import org.apache.log4j.Logger;
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
        this.periodo = new Periodo();
    }
    
    private Periodo periodo;

    public void uploadDisciplinas(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        List data;
        try {
            data = CSVParser.parse(file.getInputstream());
        } catch (IOException ex) {
            Logger.getLogger(ArquivoBean.class).error(ex.getMessage());
        }
        FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void uploadTurmas(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        List data;
        try {
            data = CSVParser.parse(file.getInputstream());
        } catch (IOException ex) {
            Logger.getLogger(ArquivoBean.class).error(ex.getMessage());
        }
        FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void uploadIndicePrioridade(FileUploadEvent event) {
        System.out.println("periodo Ajuste:" + periodo);
        UploadedFile file = event.getFile();
        List data;
        try {
            data = CSVParser.parse(file.getInputstream());
        } catch (IOException ex) {
            Logger.getLogger(ArquivoBean.class).error(ex.getMessage());
        }
        FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
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

    public void selecionaPeriodo(ValueChangeEvent event) {
        ApplicationContext context = FacesContextUtils.getWebApplicationContext(FacesContext.getCurrentInstance());        
        PeriodoDao periodoDao = (PeriodoDao) context.getBean(PeriodoDao.class);
        periodo = periodoDao.buscar((Long) event.getNewValue());
        System.out.println("value changed...");
        FacesMessage msg = new FacesMessage("selected", periodo.toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

}
