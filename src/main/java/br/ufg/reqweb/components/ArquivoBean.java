/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.components;

import br.ufg.reqweb.dao.DisciplinaDao;
import br.ufg.reqweb.dao.PeriodoDao;
import br.ufg.reqweb.model.Disciplina;
import br.ufg.reqweb.model.Periodo;
import br.ufg.reqweb.model.Semestre;
import br.ufg.reqweb.model.Turma;
import br.ufg.reqweb.util.CSVParser;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

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

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ArquivoBean() {
    }

    public void uploadDisciplinas(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        List<String[]> data;
        try {
            data = CSVParser.parse(file.getInputstream());
            System.out.println(data);
        } catch (IOException ex) {
            Logger.getLogger(ArquivoBean.class).error(ex.getMessage());
        }
        FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void uploadTurmas(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        List<String[]> data;
        Turma turma;
        ApplicationContext context = FacesContextUtils.getWebApplicationContext(FacesContext.getCurrentInstance());
        DisciplinaDao disciplinaDao = context.getBean(DisciplinaDao.class);
        PeriodoDao periodoDao = context.getBean(PeriodoDao.class);

        try {
            data = CSVParser.parse(file.getInputstream());
            String[] row;
            //primeira linha apos o cabecalho
            row = data.get(1);
            List<Periodo> periodos = periodoDao.procurar(row[1]);
            Disciplina disciplina = disciplinaDao.buscar(Long.parseLong(row[4]));
            Periodo periodo = null;
            if (periodos.size() > 0 || disciplina == null) {
                for (Periodo p : periodos) {
                    if (p.getSemestre() == Semestre.getSemestre(Integer.parseInt(row[3]))) {
                        periodo = p;
                        break;
                    }
                }

            } else {
                throw new NullPointerException();
            }
            for (int i = 1; i < data.size(); i++) {
                row = data.get(i);
                turma = new Turma();
                turma.setId(Long.parseLong(row[0]));
                turma.setNome(row[2]);
                turma.setPeriodo(periodo);
            }
            FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (IOException ex) {
            Logger.getLogger(ArquivoBean.class).error(ex.getMessage());
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao ler arquivo", null);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (NullPointerException ex) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periodo ou  Disciplina não cadastrados", null);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }

    }

    public void uploadIndicePrioridade(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        List<String[]> data;
        try {
            data = CSVParser.parse(file.getInputstream());
            System.out.println(data);	
        } catch (IOException ex) {
            Logger.getLogger(ArquivoBean.class).error(ex.getMessage());
        }
        FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
