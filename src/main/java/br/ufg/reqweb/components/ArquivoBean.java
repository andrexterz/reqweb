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
import org.springframework.stereotype.Component;

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

    public void uploadDisciplinas(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        List<String[]> data;
        disciplinaListPreview = new ArrayList<>();
        try {
            data = CSVParser.parse(file.getInputstream());
            Curso c = null;
            for (int i = 1; i < data.size(); i++) {
                String[] row = data.get(i);
                Long codigo = Long.parseLong(row[0].trim());
                String nome = row[1].trim();
                String matriz = row[2].trim();
                Disciplina d = new Disciplina();
                if (c != null) {
                    if (!c.getMatriz().equals(matriz)) {
                        c = cursoDao.findByMatriz(matriz);
                        System.out.println("row[2]: " + matriz + "\tmatriz: " + c.getMatriz());
                    }
                } else {
                    System.out.println("curso é nulo");
                    c = cursoDao.findByMatriz(matriz);

                }
                d.setCodigo(codigo);
                d.setNome(nome);
                d.setCurso(c);
                disciplinaListPreview.add(d);
            }
            disciplinaDao.adicionar(disciplinaListPreview);
        } catch (IOException ex) {
            Logger.getLogger(ArquivoBean.class).error(ex.getMessage());
        }
        FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
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
