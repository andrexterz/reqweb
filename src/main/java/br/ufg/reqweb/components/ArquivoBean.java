/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.components;

import br.ufg.reqweb.dao.CursoDao;
import br.ufg.reqweb.dao.DisciplinaDao;
import br.ufg.reqweb.model.Curso;
import br.ufg.reqweb.model.Disciplina;
import br.ufg.reqweb.model.Turma;
import br.ufg.reqweb.util.CSVParser;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author André
 */
@Component
@Scope(value = "session")
public class ArquivoBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Autowired
    private CursoDao cursoDao;

    @Autowired
    private DisciplinaDao disciplinaDao;
    private String termoBusca;
    private List<Disciplina> disciplinaListPreview;
    private final LazyDataModel<Disciplina> disciplinas;
    private List<Turma> turmas;

    public ArquivoBean() {
        disciplinaListPreview = new ArrayList<>();
        turmas = new ArrayList<>();
        termoBusca = "";
        disciplinas = new LazyDataModel<Disciplina>() {
            
            @Override
            public List<Disciplina> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
                setPageSize(pageSize);
                List<Disciplina> disciplinaList;
                if (termoBusca.equals("")) {
                    disciplinaList = disciplinaDao.find(first, pageSize);
                    setRowCount(disciplinaDao.count());
                } else {
                    disciplinaList = disciplinaDao.find(termoBusca);
                    setRowCount(disciplinaList.size());
                }
                return disciplinaList;
            }
            
        };
    }

   public void uploadTurmas(FileUploadEvent event) {

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

    public LazyDataModel<Disciplina> getDisciplinas() {
        return disciplinas;
    }

    public List<Turma> getTurmas() {
        return turmas;
    }

}
