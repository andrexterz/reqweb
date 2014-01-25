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
import br.ufg.reqweb.util.CSVParser;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author andre
 */
@Component
public class DisciplinaBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Autowired
    private CursoDao cursoDao;

    @Autowired
    private DisciplinaDao disciplinaDao;
    
    @Autowired
    private Validator validator;
    
    private String termoBusca;
    public static final String ADICIONA = "a";
    public static final String EDITA = "e";
    private String operation;
    private Disciplina disciplina;
    private Disciplina itemSelecionado;
    private List<Disciplina> disciplinaListPreview;
    private final LazyDataModel<Disciplina> disciplinas;

    public DisciplinaBean() {
        disciplinaListPreview = new ArrayList<>();
        termoBusca = "";
        operation = null;
        disciplina = new Disciplina();
        itemSelecionado = null;
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

    public void novaDisciplina(ActionEvent event) {
        setOperation(ADICIONA);
        disciplina = new Disciplina();

    }

    public void editaDisciplina(ActionEvent event) {
        if (getItemSelecionado() == null) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "info", LocaleBean.getMessageBundle().getString("itemSelecionar"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            setOperation(EDITA);
            disciplina = getItemSelecionado();
        }
    }

    public String excluiDisciplina() {
        FacesMessage msg;
        if (getItemSelecionado() == null) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "info", LocaleBean.getMessageBundle().getString("itemSelecionar"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        } else {
            disciplinaDao.excluir(itemSelecionado);
            itemSelecionado = null;
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "info", LocaleBean.getMessageBundle().getString("dadosExcluidos"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return listaDisciplinas();
        }
    }

    public void salvaDisciplinas() {
        disciplinaDao.adicionar(disciplinaListPreview);
    }

    public String salvaDisciplina() {
        FacesMessage msg;
        RequestContext context = RequestContext.getCurrentInstance();
        Set<ConstraintViolation<Disciplina>> errors = validator.validate(disciplina);
        if (errors.isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "info", LocaleBean.getMessageBundle().getString("dadosSalvos"));
            context.addCallbackParam("resultado", true);
            if (operation.equals(ADICIONA)) {
                disciplinaDao.adicionar(disciplina);
                itemSelecionado = disciplina;
            } else {
                disciplinaDao.atualizar(disciplina);
            }
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return listaDisciplinas();
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "info", LocaleBean.getMessageBundle().getString("dadosInvalidos"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            context.addCallbackParam("resultado", false);
            return null;
        }
    }

    public String listaDisciplinas() {
        return "disciplinas";
    }
    
    public void selecionaItem(SelectEvent event) {
        itemSelecionado = (Disciplina) event.getObject();
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
        } catch (IOException ex) {
            Logger.getLogger(ArquivoBean.class).error(ex.getMessage());
        }
        FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public LazyDataModel<Disciplina> getDisciplinas() {
        return disciplinas;
    }

    public String getTermoBusca() {
        return termoBusca;
    }

    public void setTermoBusca(String termoBusca) {
        this.termoBusca = termoBusca;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }

    public boolean isSelecionado() {
        return itemSelecionado != null;
    }

    public Disciplina getItemSelecionado() {
        return itemSelecionado;
    }

    public void setItemSelecionado(Disciplina itemSelecionado) {
        this.itemSelecionado = itemSelecionado;
    }

}
